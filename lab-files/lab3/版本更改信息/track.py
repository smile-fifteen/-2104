import requests
import os

ver = ['70','71','72','73','74','80','81','82','90','100','111','112']
filenum = []
T = []

category = []

solid = "feature_"

class feature:
    ver = ''
    name = ''
    

def GetNum(i):
    count = 0
    for fn in os.listdir(ver[i]):
        count = count+1
    return count-1

def Readfile():
    i = 0
    for i in range(8):
        j = 0
        for j in range(filenum[i]):
            path = ver[i]+'/'+solid+str(j)+".txt"
            f = open(path,"r",encoding = 'utf-8')
            temp =  feature()
            temp.ver = ver[i]
            temp.name = f.readline().strip()
            if temp.name != '':
                T.append(temp)
            f.close()
    return

def classify():
    if os.path.exists("type.txt"):
        return
    
    max = 0
    index = 0
    ver = ''
    for f in T:
        if ver != f.ver:
            ver = f.ver
            print(ver)
        ft = int(input(f.name+":"))
        if ft < max:
            category[ft].append(index)
        else:
            category.append([index])
            max = max+1
        index = index+1

    file = open("type.txt","w",newline='',encoding='utf-8')
    for tp in category:
        for index in tp:
            file.write(str(index)+" ")
        file.write("\r\n")
    file.close()

def track():
    fr = open("type.txt","r",encoding='utf-8')
    line = fr.readline().strip()
    
    while line:
        category.append(line.split(' '))
        line = fr.readline().strip()
    fr.close()
    fw = open("track.txt","w",newline='',encoding='utf-8')
    for i in range(len(category)):
        fw.write(T[int(category[i][0])].name+":\r\n")
        for j in range(len(category[i])):
            if(j != 0):
                fw.write("->")
            fw.write(T[int(category[i][j])].ver)
        fw.write("\r\n")
            
    fw.close()
    

def main():
    i = 0;
    for i in range(8):
        filenum.append(GetNum(i))
    Readfile()
    classify()
    track()
main()
