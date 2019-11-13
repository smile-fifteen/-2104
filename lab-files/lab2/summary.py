import re
import fileinput

class question:
    def __init__(self):
        self.str = ''     
        self.vote = 0     
        self.answer = 0
        self.views = 0

class word:
    def __init__(self):
        self.w = ''
        self.fre = 0
        self.vote = 0     
        self.answer = 0
        self.views = 0

def ReadQ(ulist):
    file = open("ide.txt", "r", encoding = 'utf=8')
    for line in file.readlines():
        a = question()
        a.str = line
        ulist.append(a)
    file.close()
    file = open("netbeans.txt", "r", encoding = 'utf=8')
    for line in file.readlines() :
        a = question()
        a.str = line
        ulist.append(a)
    file.close()
    i = 0
    for line in fileinput.input('ide_rank.txt'):
        ulist[i].vote, ulist[i].answer, ulist[i].views = line.split()
        i = i+1
    for line in fileinput.input('netbeans_rank.txt'):
        ulist[i].vote, ulist[i].answer, ulist[i].views = line.split()
        i = i+1

def initialword(wlist):
    for line in fileinput.input('word_freq.txt'):
        w = word()
        w.w,w.fre = line.split()
        wlist.append(w)

def summary(ulist, wlist):
    for u in ulist:
        for w in wlist:
            if u.str.count(w.w) > 0:
                w.vote = w.vote + (int) (u.vote)
                w.answer = w.answer + (int) (u.answer)
                w.views = w.views + (int) (u.views)

        
def main():
    ulist = [];
    wlist = [];
    ReadQ(ulist)
    initialword(wlist)
    summary(ulist, wlist)

    file = open("sortbase.txt", "w", encoding = 'utf-8')
    i = 0
    file.write("代码、文本编辑功能：\r\n")
    j = 1
    for j in range(12):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("代码分析、编译、调试功能：\r\n")
    j = 1
    for j in range(8):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("多语言编辑：\r\n")
    j = 1
    for j in range(5):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("多环境编辑：\r\n")
    j = 1
    for j in range(4):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("文件管理：\r\n")
    j = 1
    for j in range(4):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("IDE平台：\r\n")
    j = 1
    for j in range(6):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("功能扩展：\r\n")
    j = 1
    for j in range(7):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("项目管理工具：\r\n")
    j = 1
    for j in range(4):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("性能需求：\r\n")
    j = 1
    for j in range(1):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.write("-----------------------------------------------\r\n")
    file.write("功能需求：\r\n")
    j = 1
    for j in range(6):
        file.write(wlist[i].w + " " + str(wlist[i].fre) + " "+ str(wlist[i].vote) + " " + str(wlist[i].answer) + " " + str(wlist[i].views) + "\r\n")
        i = i+1
    file.close()
    
main()
