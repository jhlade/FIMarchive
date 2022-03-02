//
//  ViewController.swift
//  KMTE18
//
//  Created by Jan Hladěna on 09.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import UIKit
import CoreMotion

class ViewController: UIViewController {
    
    // model
    private lazy var model = KMTEModel()
    
    let dt : Double = 0.01
    
    private let aktivita = CMMotionActivityManager()
    private let pohyb = CMMotionManager()
    
    private var gyroDataX = 0.0
    private var gyroDataY = 0.0
    
    private var sklonXrad = 0.0
    private var sklonYrad = 0.0
    
    private var ťapoměr = CMPedometer()
    private var chodí = false
    private var vzdálenost = 0.0
    
    private var počítadloČasu = 0.0
    private var průměrnáPřesnost : Double = 1.0
    private var součetPřesnosti : Double = 0.0
    private var početMěření : UInt64 = 0
    private var časovač = Timer()
    //private var spuštěno = false
    
    // hlavní tlačítko - popis
    @IBOutlet private weak var hlavníTlačítko: UIButton!
    
    // počítadla
    @IBOutlet private weak var časPopisek: UILabel!
    @IBOutlet private weak var dráhaPopisek: UILabel!
    @IBOutlet weak var přesnostPopisek: UILabel!
    
    // hlavní animační plocha
    @IBOutlet weak var kruh: UIView!
    // zobrazovač čar
    @IBOutlet weak var grafickýDebugger: DebugCary!
    
    // pro posun kuličky
    @IBOutlet weak var kuličkaX1: NSLayoutConstraint!
    @IBOutlet weak var kuličkaY1: NSLayoutConstraint!
    @IBOutlet weak var kuličkaX2: NSLayoutConstraint!
    @IBOutlet weak var kuličkaY2: NSLayoutConstraint!
    
    private var výchozíKuličkaX1 : CGFloat = 0.0
    private var výchozíKuličkaY1 : CGFloat = 0.0
    private var výchozíKuličkaX2 : CGFloat = 0.0
    private var výchozíKuličkaY2 : CGFloat = 0.0
    
    // navigační lišta
    @IBOutlet weak var navigace: UINavigationItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        hlavníTlačítko.setTitle("Start", for: .normal)
        
        výchozíKuličkaX1 = kuličkaX1.constant
        výchozíKuličkaY1 = kuličkaY1.constant
        výchozíKuličkaX2 = kuličkaX2.constant
        výchozíKuličkaY2 = kuličkaY2.constant
        
        časovač = Timer()
        časovač = Timer.scheduledTimer(timeInterval: dt, target: self, selector: #selector(UpdateTimer), userInfo: nil, repeats: true)
        
        self.spustitSenzory()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // hlavní tlačítko - akce
    @IBAction func hlavníTlačítkoKlik(_ sender: Any) {
        // TODO změnit stav
        
        if (model.stavModelu == KMTEModel.Stavy.KONEC) {
            
            // ukončit a uložit
            model.uložit(čas: počítadloČasu, dráha: vzdálenost, přesnost: průměrnáPřesnost)
            
            print("Konec -> Start")
            
            navigace.leftBarButtonItem?.isEnabled = true
            navigace.rightBarButtonItem?.isEnabled = true
            
            self.dráhaPopisek.text = "-- m"
            
            počítadloČasu = 0.0
            vzdálenost = 0.0
            průměrnáPřesnost = 1.0
            součetPřesnosti = 0.0
            
            časPopisek.text = počítadloČasu.minuteSecondMS
            dráhaPopisek.text = String(format: "%02.02f m", vzdálenost)
            přesnostPopisek.text = String(format: "%d %%", Int(round(průměrnáPřesnost * 100)))
            
            časovač = Timer()
            časovač = Timer.scheduledTimer(timeInterval: dt, target: self, selector: #selector(UpdateTimer), userInfo: nil, repeats: true)
            
            hlavníTlačítko.setTitle("Start", for: .normal)
            
            resetKuličky()
            
            // přesměrovat pohled
            self.performSegue(withIdentifier: "scoreSegue", sender: self)
            
        } else if (model.stavModelu == KMTEModel.Stavy.PROBÍHÁ) {
            
            print("Probíhá -> Konec")
            
            časovač.invalidate()
            ťapoměr.stopUpdates()
            početMěření = 0
            součetPřesnosti = 0.0
            
            hlavníTlačítko.setTitle("Výsledky", for: .normal)
            
            navigace.leftBarButtonItem?.isEnabled = false
            navigace.rightBarButtonItem?.isEnabled = true
            
            model.zastavit()
            
        } else if (model.stavModelu == KMTEModel.Stavy.PŘIPRAVEN) {
        
            print("Připraven -> Probíhá")
            
            navigace.leftBarButtonItem?.isEnabled = false
            navigace.rightBarButtonItem?.isEnabled = false
        
            hlavníTlačítko.setTitle("Zastavit", for: .normal)
            
            self.dráhaPopisek.text = "-- m"
            
            časovač = Timer()
            časovač = Timer.scheduledTimer(timeInterval: dt, target: self, selector: #selector(UpdateTimer), userInfo: nil, repeats: true)
            
            if CMPedometer.isDistanceAvailable() {
                
                ťapoměr = CMPedometer()
                ťapoměr.startUpdates(from: Date(), withHandler: { (pedometerData, error) in
                    if let ťapData = pedometerData{
                        self.vzdálenost = Double(ťapData.distance!)
                        
                        self.chodí = false
                    } else {
                        self.chodí = false
                    }
                })
            }
            
            if CMMotionActivityManager.isActivityAvailable() {
                sledováníAktivity()
            }
            
            model.spustit()
            
        }
    }
    
    private func sledováníAktivity() {
        aktivita.startActivityUpdates(to: OperationQueue.main) {
            [weak self] (activity: CMMotionActivity?) in
            
            guard let activity = activity else { return }
            DispatchQueue.main.async {
                if activity.walking {
                    self?.chodí = true
                } else if activity.stationary {
                    self?.chodí = false
                } else if activity.running {
                    self?.chodí = true
                } else if activity.automotive {
                    self?.chodí = true
                }
            }
        }
    }
    
    private func posunKuličky(x: Double, y: Double) {
        
        // meze koulení -> stop
        let okraj: CGFloat = (sqrt(kuličkaX1.constant * kuličkaX1.constant + kuličkaY1.constant * kuličkaY1.constant))
        let maxOkraj = (kruh.frame.size.width/2.0)
        
        // průběžný výpočet přesnosti
        početMěření += 1
        součetPřesnosti += Double(abs(maxOkraj - okraj)/maxOkraj)
        průměrnáPřesnost = součetPřesnosti / Double(početMěření)
        
        // vypadává z kruhu
        if (okraj < maxOkraj) {
        
            // s += g*sin(alpha)*dt*dt
            kuličkaX1.constant += CGFloat(model.g * sin(sklonXrad))
            kuličkaX2.constant += CGFloat(model.g * sin(sklonXrad))
            
            kuličkaY1.constant += CGFloat(model.g * sin(sklonYrad))
            kuličkaY2.constant += CGFloat(model.g * sin(sklonYrad))
            
        } else {
            model.zastavit()
            časovač.invalidate()
            ťapoměr.stopUpdates()
            početMěření = 0
            součetPřesnosti = 0.0
            
            hlavníTlačítko.setTitle("Výsledky", for: .normal)
            
            navigace.leftBarButtonItem?.isEnabled = false
            navigace.rightBarButtonItem?.isEnabled = true
        }
        
    }
    
    private func resetKuličky() {
        kuličkaX1.constant = výchozíKuličkaX1
        kuličkaX2.constant = výchozíKuličkaX2
        kuličkaY1.constant = výchozíKuličkaY1
        kuličkaY2.constant = výchozíKuličkaY2
    }
    
    @objc func UpdateTimer() {
        
        if model.stavModelu == KMTEModel.Stavy.PROBÍHÁ {
            počítadloČasu = počítadloČasu + dt
            časPopisek.text = počítadloČasu.minuteSecondMS
            přesnostPopisek.text = String(format: "%d %%", Int(round(průměrnáPřesnost * 100)))
        }
        
        // gyroskop
        if pohyb.isGyroAvailable && pohyb.gyroData != nil {
            self.gyroDataX = pohyb.gyroData!.rotationRate.x
            self.gyroDataY = pohyb.gyroData!.rotationRate.y
        } else {
            // TODO havárie!
            self.gyroDataX = 0.0
            self.gyroDataY = 0.0
        }
        
        // pouze za běhu
        if model.stavModelu == KMTEModel.Stavy.PROBÍHÁ {
            //posunKuličky(x: self.gyroDataX, y: self.gyroDataY)
            posunKuličky(x: self.sklonXrad/Double.pi*2, y: self.sklonYrad/Double.pi*2)
        }
        
        if model.stavModelu == KMTEModel.Stavy.PROBÍHÁ || model.stavModelu == KMTEModel.Stavy.PŘIPRAVEN {
            if KMTEModel.zobrazitDebugger {
                //grafickýDebugger.překreslitČáru(zrychleníX: self.gyroDataX, zrychleníY: self.gyroDataY)
                grafickýDebugger.překreslitČáru(zrychleníX: self.sklonXrad/Double.pi*2, zrychleníY: self.sklonYrad/Double.pi*2)
            }
        }
        
        
        // ťapoměr
        if model.stavModelu == KMTEModel.Stavy.PROBÍHÁ {
            if CMPedometer.isDistanceAvailable() {
                if (chodí) {
                    dráhaPopisek.text = String(format: "🐾%02.01f m", vzdálenost)
                } else {
                    dráhaPopisek.text = String(format: "✋🏻%02.01f m", vzdálenost)
                }
            } else {
                dráhaPopisek.text = "-- m"
            }
        }
    }
    
    func spustitSenzory() {
        self.pohyb.gyroUpdateInterval = dt // 1.0 / 60.0
        self.pohyb.startGyroUpdates()
        
        self.pohyb.deviceMotionUpdateInterval = dt // 1.0 / 60.0
        self.pohyb.startDeviceMotionUpdates(to: OperationQueue()) { [weak self] (motion, error) -> Void in
        
            if let sklon = motion?.attitude {
                DispatchQueue.main.async {
                    self?.sklonXrad = sklon.roll
                    self?.sklonYrad = sklon.pitch
                }
            }
        }
        
    }
    
    func zastavitSenzory() {
        self.pohyb.stopGyroUpdates()
        self.pohyb.stopDeviceMotionUpdates()
    }
    

}

