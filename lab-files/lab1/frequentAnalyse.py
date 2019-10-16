def getText():
    txt = open("ide.txt","r", encoding='utf-8').read()
    txt = txt.lower()#小写化
    for ch in '|"#$&()*+,-./:;<=>?@[]\\^{|}!~':
        txt = txt.replace(ch," ")
    return txt

hamlettext = getText()
words = hamlettext.split()
counts ={}
stopword = ["how","is","what","what's","which","are","why","for","as","not","java","in", "ide","idea", 
            "a", "in","and", "i", "the", "on","or", "do",  "visual", "0", "7", "intellij", "any", "why",
            "with", "of", "c", "eclipse", "an", "studio", "visual studio", "The", "to", "can", "netbeans",
            "there", "does", "that", "it", "all", "3", "2", "1","4", "5", "6","8","9", "like", "up", "work", 
            "when", "use", "using","my", "from", "get", "you", "after"]#停用词,仅全小写有效

for word in words:
    if word not in stopword:
        counts[word] = counts.get(word,0)+1
items = list(counts.items())
items.sort (key=lambda x:x[1], reverse=True)
with open('ide_frequence.txt','wt') as f:
    for i in range(150):#出词数量
        word,count = items[i]
        print ("{0:<10}{1:^10}".format(word,count), file = f)
