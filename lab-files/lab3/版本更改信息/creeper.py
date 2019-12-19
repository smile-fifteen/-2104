import requests
from bs4 import BeautifulSoup
import bs4

ver = ['70','71','72','73','74','80','81','82','90','100','111','112']

def GenerateUrl(i):
    url0 = "https://netbeans.org/community/releases/"
    url1 = "http://netbeans.apache.org/download/nb"
    if i < 8:
        return url0+ver[i]
    else:
        return url1+ver[i]
    
def GetHtmlText(url):
    try:
        r = requests.get(url,timeout = 60)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        return r.text
    except:
        return "error"

def Findfeature(html, i):
    html = html.replace('<br>','')
    html = html.replace('<tt>','')
    html = html.replace('</tt>','')
    soup = BeautifulSoup(html,"html.parser")
    if i < 7:
        for h in soup('h2'):
            if isinstance(h,bs4.element.Tag):
                table = h.next_sibling.next_sibling
                j = 0
                for td in table('td'):
                    ulist = []
                    mark = 0
                    for p in td('p'):
                        index = 0
                        temp = ''
                        for index in range(len(p.contents)):
                            temp += p.contents[index].string
                        temp = temp.replace("\n","")
                        ulist.append([temp])
                        #mark = 1
                    #for li in td('li'):
                        #temp = li.string
                        #print(temp)
                        #temp = temp.replace("\n","")
                        #ulist.append([temp])
                    #if mark == 1:
                        #WriteFiles(i,j,ulist)
                        #j = j+1
                        br = p.next_sibling.next_sibling
                        if br is not None:
                            Findstr(ulist,br)
                            WriteFiles(i,j,ulist)
                            j = j+1
                        ulist.clear()


                        
def Findstr(ulist,br):
    if br.string is not None:
        temp = br.string
        temp = temp.replace("\n","")
        ulist.append([temp])
    else:
        for child in br:
            Findstr(ulist,child)
            
        
def WriteFiles(i,j,ulist):
    f = open(ver[i]+"/"+"feature_"+str(j)+".txt","w",newline='',encoding='utf-8')
    for line in ulist:
        f.write(line[0]+'\r\n')
    
                

def main():
    i = 0
    for i in range(7):
        url = GenerateUrl(i)
        html = GetHtmlText(url)
        Findfeature(html,i)
main()
