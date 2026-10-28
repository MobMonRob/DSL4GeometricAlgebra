import numpy as np
import clifford as cf

# 1. INITIALISIERUNG DER PGA(3,0,1) ALGEBRA
layout, blades = cf.ClInterface.generate_spaces(sig=[0, 1, 1, 1])

# Korrektes Mapping: e0 steht vorne (Index 1 in Clifford)
e0 = blades['e1']  # e0^2 = 0
e1 = blades['e2']  # e1^2 = 1
e2 = blades['e3']  # e2^2 = 1
e3 = blades['e4']  # e3^2 = 1

def create_dh_motor(theta, d, a, alpha):
    """
    Erzeugt den korrekten PGA-Transformationsmotor mittels Bivektor-Generatoren.
    """
    # Z-Schraubung: Rotation um e1^e2, Translation entlang e3 mittels e0^e3
    R_z = np.cos(theta / 2.0) - np.sin(theta / 2.0) * (e1 ^ e2)
    T_z = 1.0 - 0.5 * d * (e0 ^ e3)
    M_z = T_z * R_z
    
    # X-Schraubung: Translation entlang e1 mittels e0^e1, Rotation um e2^e3
    T_x = 1.0 - 0.5 * a * (e0 ^ e1)
    R_x = np.cos(alpha / 2.0) - np.sin(alpha / 2.0) * (e2 ^ e3)
    M_x = T_x * R_x
    
    return M_z * M_x

def extract_cartesian_position(p_global):
    # Homogene Komponente auf dem euklidischen Ursprungspunkt (e1^e2^e3)
    w = p_global[blades['e234']] 
    if abs(w) < 1e-6:
        w = 1.0
        
    # Extraktion über die korrekten Tri-Ebenen mit e0-Anteil
    x = p_global[blades['e134']] / w  # e0 ^ e2 ^ e3
    y = p_global[blades['e124']] / w  # e0 ^ e1 ^ e3
    z = p_global[blades['e123']] / w  # e0 ^ e1 ^ e2
    return x, y, z

# UR5e DH-Parameter (in Metern)
ur5e_dh = [
    {'d': 0.1625,  'a': 0.0,      'alpha': np.radians(90.0)},   # Base
    {'d': 0.0,     'a': -0.425,   'alpha': np.radians(0.0)},    # Shoulder
    {'d': 0.0,     'a': -0.3922,  'alpha': np.radians(0.0)},    # Elbow
    {'d': 0.1333,  'a': 0.0,      'alpha': np.radians(90.0)},   # Wrist 1
    {'d': 0.0997,  'a': 0.0,      'alpha': np.radians(-90.0)},  # Wrist 2
    {'d': 0.0996,  'a': 0.0,      'alpha': np.radians(0.0)}     # Wrist 3
]

# Gelenkwinkel für die absolute Neutralstellung
neutral_q = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

# Kinematische Kette berechnen
M_total = layout.MultiVector(1.0)
for i, link in enumerate(ur5e_dh):
    M_i = create_dh_motor(neutral_q[i], link['d'], link['a'], link['alpha'])
    M_total = M_total * M_i

# Lokaler euklidischer Punkt im Ursprung (e1^e2^e3)
P_local = e1 * e2 * e3

# Transformation via Sandwich-Produkt
P_global = M_total * P_local * (~M_total)

x, y, z = extract_cartesian_position(P_global)

print("=== UR5e FK Korrigiert (Echte Bivektor-Generatoren) ===")
print(f"X = {x:.4f} m\nY = {y:.4f} m\nZ = {z:.4f} m")
