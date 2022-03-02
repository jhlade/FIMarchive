//
//  SettingsViewController.swift
//  KMTE18
//
//  Created by Jan Hladěna on 09.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import UIKit

class SettingsViewController: UIViewController, UITextFieldDelegate {
    
    @IBOutlet weak var jménoHráče: UITextField!
    @IBOutlet weak var citlivostPosuvník: UISlider!
    @IBOutlet weak var zobrazovatSměrIndikátor: UISwitch!
    @IBOutlet weak var citlivostPopisek: UILabel!
    
    @IBAction func nastavitJméno(_ sender: Any) {
        KMTEModel.jméno = jménoHráče.text!
    }
    
    @IBAction func nastavitCitlivost(_ sender: Any) {
        KMTEModel.citlivost = citlivostPosuvník.value
        citlivostPopisek.text = String(format: "%.1f", KMTEModel.citlivost)
    }
    
    @IBAction func zobrazovatSměr(_ sender: Any) {
        if (zobrazovatSměrIndikátor.isOn) {
            KMTEModel.zobrazitDebugger = true
        }
        if (!zobrazovatSměrIndikátor.isOn) {
            KMTEModel.zobrazitDebugger = false
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        zobrazovatSměrIndikátor.isOn = KMTEModel.zobrazitDebugger
        
        citlivostPopisek.text = String(format: "%.1f", KMTEModel.citlivost)
        citlivostPosuvník.value = KMTEModel.citlivost
        
        jménoHráče.text = KMTEModel.jméno
        
        self.jménoHráče.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    /*
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
    }*/
    
    // schování klávesnice
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }

}
