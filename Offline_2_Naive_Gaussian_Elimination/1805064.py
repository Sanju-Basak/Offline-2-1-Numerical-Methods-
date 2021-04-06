# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 20:29:03 2021

@author: Sanju Basak
"""
import numpy as np

def GaussianElimination(A, B, d= True):
    x= np.zeros((n,1))
#Forward Elimination
    for i in range(n-1):
        for j in range(i+1, n):
            temp= (A[j, i]/A[i, i])  
            A[j, :]= A[j, :]- temp * A[i, :]
            B[j, :]= B[j, :]- temp * B[i, :]
            if(d is True):
                print("Matrix A : " )
                print(A)
                print("Matrix B : ")
                print(B)
#Back Substitution
    for i in range(n-1, -1, -1):
        C=np.dot(A[i, :],x)
        x[i, 0]= (B[i, 0]- C)/ A[i, i]
    return x
    
        
    
n= int(input())
mat1= np.zeros((n,n)) 
mat2= np.zeros((n,1))

for i in range(n):
    mat1[i]= input().split(" ")

for i in range(n):
    mat2[i]= input();

mat= GaussianElimination(mat1, mat2)

for i in range(n):
    print(format(mat[i,0], "0.4f"))
