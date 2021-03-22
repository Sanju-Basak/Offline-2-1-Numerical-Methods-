import math
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

def f(x):
    return ((x/(1-x))*math.sqrt(6.0/(2+x)))- 0.05

x= []
y= []
j= .01
for i in range(100):
    x.append(j)
    calc= f(j)
    y.append(calc)
    j= j+.001

plt.plot(x, y, color= "red")
plt.axhline(y=0, color= "black", linestyle=":")
plt.axvline(x=0, color= "black", linestyle=":")
plt.xlabel("Mole fraction",)
plt.ylabel("f(x) = ((x/(1-x))*sqrt(6.0/(2+x)))- 0.05")
plt.grid()
     
def bisection(xlower, xupper, rel_error, max_iter):
    c=0
    i=1
    while(i<=max_iter):
        xmid= (xlower+ xupper)/2        
        if(f(xlower)*f(xmid)<0):
            xupper= xmid
        elif(f(xlower)*f(xmid)>0):
            xlower= xmid
        else:
            return xmid
        if(i>1):
            rel_app= abs(((xmid-c)/xmid)*100)
            if(rel_app<rel_error):
                return xmid
        c= xmid
        i= i+1
    return xmid

x= bisection(0.02, .041, .5, 20)
print("Mole fraction is:", x)

def bisection2(xlower, xupper):
    c=0
    a= list()
    i=1
    while(i<=20):
        xmid= (xlower+ xupper)/2        
        if(f(xlower)*f(xmid)<0):
            xupper= xmid
        elif(f(xlower)*f(xmid)>0):
            xlower= xmid
        else:
            return xmid
        if(i>1):
            rel_app= abs(((xmid-c)/xmid)*100)
            a.append(rel_app)
            
        c= xmid
        i= i+1
    return a

x= bisection2(.02, .041)

index= np.arange(2, 21);
df= pd.DataFrame(x,index, columns=["Absolute Rel Approx Error"])
print(df)


        
    

