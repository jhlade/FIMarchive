//
//  ModelKMTE.swift
//  KMTE18
//
//  Hlavní model aplikace
//
//  Created by Jan Hladěna on 09.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import Foundation

class KMTEModel {
    
    enum Stavy {
        case PŘIPRAVEN, PROBÍHÁ, KONEC
    }
    
    // klíč pro REST/JSON komunikaci
    static let APIKEY : String = "0e9b349ed7d5154c1b3f1e96440178f9"
    // REST URL
    static let RESTurl : String = "https://kmte.joutsen.cz/rest.php"
    
    // nastavení + výchozí hodnoty
    static var citlivost : Float {
        get {
            return (UserDefaults.standard.float(forKey: "citlivost") != 0.0) ? UserDefaults.standard.float(forKey: "citlivost") : 1.0
        }
        set(nováCitlivost) {
            UserDefaults.standard.set(nováCitlivost, forKey: "citlivost")
            
            print("Proběhlo nastavení citlivosti na \(nováCitlivost).")
        }
    }
    
    static var jméno : String {
        get {
            return (UserDefaults.standard.string(forKey: "jmeno") != nil) ? UserDefaults.standard.string(forKey: "jmeno")! : "KMTE usr1"
        }
        set(novéJméno) {
            UserDefaults.standard.set(novéJméno, forKey: "jmeno")
            
            print("Nové jméno je nyní \(novéJméno)")
        }
    }
    
    static var zobrazitDebugger : Bool {
        get {
            return UserDefaults.standard.bool(forKey: "debugger")
        }
        set(novýDebugger) {
            UserDefaults.standard.set(novýDebugger, forKey: "debugger")
            
            if novýDebugger {
                print("Zobrazování čáry je povoleno.")
            } else {
                print("Zobrazování čáry je zakázáno.")
            }
        }
    }
    
    // stav modelu
    private(set) var stavModelu: Stavy = Stavy.PŘIPRAVEN
    
    // výsledky
    private var čas : Double
    private var dráha : Double
    private var přesnost : Int
    
    // fyzkikální vlatnosti
    public var g : Double {
        get {
            return 9.81 * 2/5 * Double(KMTEModel.citlivost)
        }
    }
    
    // Fp = Fg * sin(alpha)
    // ma = mg * sin(alpha) / m
    // a = g * sin(alpha)
    // v = v0 + a*dt // v += a*dt
    // v = s/dt; s/dt += a*dt /*dt
    // s += a*dt*dt (takže při 0.01 -> 0.01*0.01)
    // s += g*sin(alpha)*dt*dt
    
    // inicializace
    init() {
        
        stavModelu = Stavy.PŘIPRAVEN
        
        čas = 0.0
        dráha = 0.0
        přesnost = 0
    }
    
    public func zastavit() {
        if stavModelu == Stavy.PROBÍHÁ {
            stavModelu = Stavy.KONEC
        }
    }
    
    public func spustit() {
        if stavModelu == Stavy.PŘIPRAVEN {
            stavModelu = Stavy.PROBÍHÁ
            
            self.čas = 0.0
            self.dráha = 0.0
            self.přesnost = 0
        }
    }
    
    public func uložit(čas: Double, dráha: Double, přesnost: Double) {
        if stavModelu == Stavy.KONEC {
            stavModelu = Stavy.PŘIPRAVEN
            
            self.čas = čas
            self.dráha = dráha
            self.přesnost = Int(round(přesnost * 100))
            
            zapsatRekord()
        }
    }
    
    // func zapsatRekord
    private func zapsatRekord() {
        
        let adresa = URL(string: KMTEModel.RESTurl)
        
        let dataDotazu : [String : String] = ["apikey" : KMTEModel.APIKEY, "type" : "create", "name" : KMTEModel.jméno, "runtime" : String(self.čas * 100), "distance" : String(Int(round(self.dráha))), "avg" : String(self.přesnost)]
        
        let jsonDotaz = try? JSONSerialization.data(withJSONObject: dataDotazu)
        var dotaz : URLRequest = URLRequest(url: adresa!)
        dotaz.httpMethod = "POST"
        dotaz.setValue("application/json;charset=utf-8", forHTTPHeaderField: "Content-Type")
        dotaz.setValue("application/json", forHTTPHeaderField: "Accept")
        dotaz.httpBody = jsonDotaz
        
        let relace = URLSession.shared.dataTask(with: dotaz) {(data, response, error ) in
            
            guard error == nil else {
                print("Došlo k chybě při komunikaci se serverem.")
                return
            }
            
            guard let content = data else {
                print("Nebyla získána žádná data.")
                return
            }
            
            guard let json = (try? JSONSerialization.jsonObject(with: content, options: JSONSerialization.ReadingOptions.mutableContainers)) as? [String : String] else {
                print("Nebyla získána data ve formátu JSON.")
                return
            }
            
            print("\(json)")
            
            }.resume()
        
    }
    
} // třída

// zobrazezní formátu času
extension TimeInterval {
    var minuteSecondMS: String {
        return String(format: "%02d:%02d.%02d", minuta, sekunda, milisekunda)
    }
    var minuta: Int {
        return Int((self / 60).truncatingRemainder(dividingBy: 60))
    }
    var sekunda: Int {
        return Int(truncatingRemainder(dividingBy: 60))
    }
    var milisekunda: Int { // jako Int na dvě des. místa - děleno 10
        return Int((self * 1000).truncatingRemainder(dividingBy: 1000)/10)
    }
}
