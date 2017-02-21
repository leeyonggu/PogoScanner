import os, sys
import json

if len(sys.argv) != 3:
	sys.exit("Usage : python getpokestop.py [input] [output]")

inputFilePath = sys.argv[1] 
outputFilePath = sys.argv[2]

outputFile = open(outputFilePath, 'w')
inputFile = open(inputFilePath, 'r')
lines = inputFile.readlines()

jsonStr = ""
for line in lines:
	jsonStr += line

dict = json.loads(jsonStr)
cells = dict['cells']
for item in cells:
	cell = cells[item]
	pokestoplist = cell['pokestop']['list']
	for pokestop in pokestoplist:
		outputLine = pokestop['pokestop_id'] + ',name,' + pokestop['latitude'] + ',' + pokestop['longitude'] + ',0.0'
		print(outputLine)
		outputFile.write(outputLine + '\n')

inputFile.close()
outputFile.close()

