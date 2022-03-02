//
//  DebugCary.swift
//  KMTE18
//
//  Created by Jan Hladěna on 13.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import UIKit

class DebugCary: UIView {
    
    private var směrX = 0.0
    private var směrY = 0.0
    
    private var velikostX = 0.0
    private var velikostY = 0.0
    
    // absolutní velikost od nuly
    private var vzdálenost : Double {
        get {
            return sqrt(směrX*směrX + směrY*směrY)
        }
    }
    
    override func draw(_ rect: CGRect) {
        
        let okraj = CGFloat(2.5)
        
        let velikost = CGPoint(x: (self.frame.size.width - 2 * okraj), y: (self.frame.size.height - 2 * okraj))
        
        self.velikostX = Double(velikost.x)
        self.velikostY = Double(velikost.y)
    
        var path = UIBezierPath()
        // střed -> cíl
        path.move(to: CGPoint(x: velikost.x / 2, y: velikost.y / 2))
        path.addLine(to: CGPoint(x: (velikost.x / 2) + CGFloat(směrX), y: (velikost.y / 2) + CGFloat(směrY)))
        
        UIColor.blue.setStroke()
        path.lineWidth = 1 * okraj
        path.stroke()
    }

    public func překreslitČáru(zrychleníX x: Double, zrychleníY y: Double) {
     
        self.směrX = x * (velikostX)
        self.směrY = y * (velikostY)
        
        // překreslení
        self.setNeedsDisplay()
    }
    
}
