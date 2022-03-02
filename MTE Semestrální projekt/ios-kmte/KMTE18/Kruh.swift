//
//  Kruh.swift
//  KMTE18
//
//  Created by Jan Hladěn on 09.06.18.
//  Copyright © 2018 Jan Hladěna. All rights reserved.
//

import UIKit

class Kruh: UIView {
    
    override func draw(_ rect: CGRect) {
        
        let okraj = CGFloat(2.5)
        
        let velikost = CGPoint(x: (self.frame.size.width - 2 * okraj), y: (self.frame.size.height - 2 * okraj))
        
        let střed = CGPoint(x: self.frame.size.width / 2.0, y: self.frame.size.height / 2.0)
        
        var path = UIBezierPath()
        path = UIBezierPath(ovalIn: CGRect(x: okraj, y: okraj, width: velikost.x, height: velikost.y))
        UIColor.red.setStroke()
        path.lineWidth = 2 * okraj
        path.stroke()
        
        UIColor.red.setStroke()
        let path2 = UIBezierPath()
        path2.move(to: CGPoint(x: střed.x, y: 0))
        path2.addLine(to: CGPoint(x: střed.x, y: velikost.y))
        path2.lineWidth = okraj
        path2.stroke()
        
        let path3 = UIBezierPath()
        path3.move(to: CGPoint(x: 0, y: střed.y))
        path3.addLine(to: CGPoint(x: velikost.x, y: střed.y))
        path3.lineWidth = okraj
        path3.stroke()
        
    }

}
