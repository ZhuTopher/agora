import os
import xml.etree.ElementTree as ET
import sys
from time import sleep

indir = "C:/Users/R Redelmeier/Desktop/rawCensusData.xml"

# Change dir as necessary

tree = ET.parse(indir)
root = tree.getroot()

cntr = 0

f = open('workfile.txt','w+')

def uprint(*objects, sep=' ', end='\n', file=sys.stdout):
    enc = file.encoding
    if enc == 'UTF-8':
        print(*objects, sep=sep, end=end, file=file)
    else:
        f = lambda obj: str(obj).encode(enc, errors='replace').decode(enc)
        print(*map(f, objects), sep=sep, end=end, file=file)

polyNum = 0
vertNum = 0

for i,tract in enumerate(root):
    tractData = tract[0]
    if tract.tag != "{http://www.opengis.net/gml}boundedBy":
        cmaname = tractData.find('{http://www.safe.com/gml/fme}CMANAME').text
        tractId = tractData.find('{http://www.safe.com/gml/fme}CTUID').text
        if cmaname == "Hamilton":
            polyNum +=1
            #print(polyNum)
            for tract in tractData.iter('{http://www.opengis.net/gml}posList'):
                vertNum += tract.text.count(" ")
                print(vertNum)
                f.write(tractId+","+tract.text.replace(" ",",")+"\n")

def countIter(iterable):
    num = 0
    for item in iterable:
        num+=1
    return num
