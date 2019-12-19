import requests
import os
import re

ver = ['70','71','72','73','74','80','81','82','90','100','111','112']
solid = "feature_"
filenum = []

def GetNum(i):
    count = 0
    for fn in os.listdir(ver[i]):
        count = count+1
    return count-1

def Readfile(key):
    i = 0
    fw = open(key+".txt","w",newline='',encoding='utf-8')
    for i in range(8):
        j = 0
        
        for j in range(filenum[i]):
            path = ver[i]+'/'+solid+str(j)+".txt"
            f = open(path,"r",encoding = 'utf-8')
            temp = f.readline().lower()
            while temp:
                if temp.find(key)>= 0:
                    fw.write(path+'\t'+temp+'\r\n')
                temp = f.readline().lower()
            f.close()

    fw.close
    return

def main():
    i = 0;
    for i in range(8):
        filenum.append(GetNum(i))
    key = input("输入关键词:")
    Readfile(key)
    
main()
