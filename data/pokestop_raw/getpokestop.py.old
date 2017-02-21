import os, sys
import json

if len(sys.argv) != 3:
	sys.exit("Usage : python getpokestop.py [input] [output]")

inputFilePath = sys.argv[1] 
outputFilePath = sys.argv[2]

outputFile = open(outputFilePath, 'w')
inputFile = open(inputFilePath, 'r')
for line in inputFile:
	dict = json.loads(line)
	ps = dict['pokestop']
	stoplist = ps['list']
	for stop in stoplist:
		outputLine = stop['pokestop_id'] + ',name,' + stop['latitude'] + ',' + stop['longitude'] + ',0.0'
		#print(outputLine)
		outputFile.write(outputLine + '\n')

outputFile.close()

