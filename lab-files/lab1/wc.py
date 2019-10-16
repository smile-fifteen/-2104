# -*- coding: utf-8 -*-

from PIL import Image
import matplotlib.pyplot as plt
from wordcloud import WordCloud,STOPWORDS,ImageColorGenerator
import jieba
import numpy as np
import jieba.posseg as pseg
from jieba import analyse

#stop = [line.strip().encode('utf-8') for line in open('stop_words.txt').readlines() ]
stop = []
filename="netbeans.txt"
f = open(filename,"r",encoding='utf-8')
outstr=""

for line in f.readlines():
    line=line.strip()
    if not len(line):
        continue
    word_list=jieba.cut(line,cut_all=False)
    for word in word_list:
        if word not in stop:
            if word !='\t' and word !='\n':
                outstr+=word
                outstr+=" "
f.close()

#backgroud_Image = Image.open(r'cloud.jpg')
#img_array = np.array(backgroud_Image)
font=r'C:\Windows\Fonts\simhei.ttf'
stopword=["how","is","what","what's","which","are","why","for","as","not","java","in", "ide","idea", 
            "a", "in","and", "i", "the", "on","or", "do",  "visual", "0", "7", "intellij", "any", "why",
            "with", "of", "c", "eclipse", "an", "studio", "visual studio", "The", "to", "can", "netbeans",
            "there", "does", "that", "it", "all", "3", "2", "1","4", "5", "6","8","9", "like", "up", "work", 
            "when", "use", "using","my", "from", "get", "you", "after"] #停用词,不区分大小写
wc = WordCloud( background_color = 'white',    # 设置背景颜色
               # mask = img_array,        # 设置背景图片
                max_words = 300,            # 设置最大现实的字数
                stopwords = stopword,        # 设置停用词
                font_path =font,# 设置字体格式，如不设置显示不了中文
                max_font_size = 30,            # 设置字体最大值
                random_state = 5,            # 设置有多少种随机生成状态，即有多少种配色方案
                scale=8,
                )
wc.generate(outstr)
plt.imshow(wc)
plt.axis('off')
plt.show()
