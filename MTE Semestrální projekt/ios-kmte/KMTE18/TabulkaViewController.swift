//
//  TabulkaViewController.swift
//  KMTE18
//
//  Created by Jan Hladěna on 14.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import UIKit

class TabulkaViewController: UITableViewController {
    
    struct Skóre: Codable {
        let name: String?
        let runtime: String?
        let distance: String?
        let avg: String?
    }

    var dataSkóre: [Skóre] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.dataSource = self
        self.tableView.delegate = self

        získatVýsledky()
    }
    
    func získatVýsledky() {
        
        let adresa = URL(string: KMTEModel.RESTurl)
        
        let dataDotazu : [String : String] = ["apikey" : KMTEModel.APIKEY, "type" : "fetch"]
        let jsonDotaz = try? JSONSerialization.data(withJSONObject: dataDotazu)
        var dotaz : URLRequest = URLRequest(url: adresa!)
        dotaz.httpMethod = "POST"
        dotaz.setValue("application/json;charset=utf-8", forHTTPHeaderField: "Content-Type")
        dotaz.setValue("application/json", forHTTPHeaderField: "Accept")
        dotaz.httpBody = jsonDotaz
        
        let relace = URLSession.shared.dataTask(with: dotaz) {(data, response, error ) in
            
            guard let data = data else {return}
            
            do {
                let výsledky = try JSONDecoder().decode([Skóre].self, from: data)
                
                if výsledky != nil {
                    self.dataSkóre = výsledky
                    
                } else {
                    print("Nebyla získána žádná data.")
                }
                print("\(self.dataSkóre)")
                
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            } catch let jsonErr {
                print("Nebyla získána data ve formátu JSON.")
            }
            
        }.resume()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

}

extension TabulkaViewController {
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.dataSkóre.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let buňka = tableView.dequeueReusableCell(withIdentifier: "Bunka", for: indexPath) as UITableViewCell
        
        if let řádekBuňka = buňka as? Sko_reR_a_dek {
            řádekBuňka.buňkaJméno.text = self.dataSkóre[indexPath.row].name
            řádekBuňka.buňkaDráha.text = self.dataSkóre[indexPath.row].distance! + " m"
            řádekBuňka.buňkaPřesnost.text = self.dataSkóre[indexPath.row].avg! + " %"
            řádekBuňka.buňkaČas.text = Double(Int(self.dataSkóre[indexPath.row].runtime!)!/100).minuteSecondMS
        }
        
        
        return buňka
    }
}
