#!BPY
import bpy
import math
from math import *
import os
try:
  os.chdir("blenderrenderer")
except:
  try:
    os.chdir("../blenderrenderer")
  except:
    nothing = "nothing"
f = Blender.Get('curframe')
if f > 0 and f <= 10:
  exec file("frames0.py")

