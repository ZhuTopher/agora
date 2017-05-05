import os
import xml.etree.ElementTree as ET
import sys
from time import sleep

indir = "/home/chris/Desktop/irl/coding/github/agora_test/census_tracts/statscan_2011_census_tracts.xml"

# Change dir as necessary

tree = ET.parse(indir)
root = tree.getroot()

cntr = 0

f = open('workfile.txt','w+')


# def uprint(*objects, sep=' ', end='\n', file=sys.stdout):
#     enc = file.encoding
#     if enc == 'UTF-8':
#         print(*objects, sep=sep, end=end, file=file)
#     else:
#         f = lambda obj: str(obj).encode(enc, errors='replace').decode(enc)
#         print(*map(f, objects), sep=sep, end=end, file=file)


polyNum = 0
totalVert = 0

for i,tract in enumerate(root):
    tractData = tract[0]
    if tract.tag != "{http://www.opengis.net/gml}boundedBy":
        # print list(tractData)
        cmaname = tractData.findtext('{http://www.safe.com/gml/fme}CMANAME')
        tractId = tractData.findtext('{http://www.safe.com/gml/fme}CTUID')
        if cmaname == "Kitchener - Cambridge - Waterloo":
            polyNum +=1
            for tract in tractData.iter('{http://www.opengis.net/gml}posList'):
                numVert = tract.text.count(" ")
                totalVert += numVert
                print(str(tractId) + ": " + str(numVert) + " vertices.\n")
                f.write(tractId+","+tract.text.replace(" ",",")+"\n")

print("Total Polygons: " + str(polyNum) + "\n")
print("Total Vertices: " + str(totalVert) + "\n")

# def countIter(iterable):
#     num = 0
#     for item in iterable:
#         num+=1
#     return num
