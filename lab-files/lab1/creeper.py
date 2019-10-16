import requests
from bs4 import BeautifulSoup
import bs4

def GenerateUrl(i, select):
    url0 = "https://stackoverflow.com/questions/tagged/netbeans?tab=active&page="
    url1 = "https://stackoverflow.com/questions/tagged/ide?tab=active&page="
    if select == 0:
        return url0+str(i)
    else:
        return url1+str(i)
    
def GetHtmlText(url):
    try:
        r = requests.get(url,timeout = 60)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        return r.text
    except:
        return "error"

def FindQuestion(ulist, html):
    soup = BeautifulSoup(html, "html.parser")
    i = 3
    for h in soup('h3'):
        if isinstance(h, bs4.element.Tag):
            if i > 0:
                i = i - 1
            else:
                ulist.append([h.string])

def WriteFiles(ulist,file):
    for question in ulist:
        file.write(question[0])
        file.write('\r\n')
                

def main():
    select = 0
    max_page = 0
    if select == 0:
        max_page = 451
        f=open("netbeans.txt","w",newline='', encoding='utf-8')
    else:
        max_page = 187
        f =open("ide.txt","w",newline='', encoding='utf-8')

    i = 1
    for i in range(max_page):
        url = GenerateUrl(i, select)
        html = GetHtmlText(url)
        ulist = []
        FindQuestion(ulist, html)
        WriteFiles(ulist,f)
    f.close()
main()
