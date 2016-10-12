import sys
import numpy as np
import math
from tkinter import *
import matplotlib
matplotlib.use("TkAgg")
from matplotlib import pyplot as plt

res = [0,0,0]

def genData(I, J, mean_demand, mean_cost, mean_capacity, stdev_demand, stdev_cost, stdev_capacity, stdev_demand_x, stdev_demand_y, stdev_facility_x, stdev_facility_y):

    I = int(I)
    J = int(J)
    mean_demand = float(mean_demand)
    mean_cost = float(mean_cost)
    mean_capacity = float(mean_capacity)
    stdev_demand = float(stdev_demand)
    stdev_cost = float(stdev_cost)
    stdev_capacity = float(stdev_capacity)
    stdev_demand_x = float(stdev_demand_x)
    stdev_demand_y = float(stdev_demand_y)
    stdev_facility_x = float(stdev_facility_x)
    stdev_facility_y = float(stdev_facility_y)

    r = []

    r.append(I)
    r.append(J)

    r.append("\n")
    for val in np.random.normal(mean_demand, stdev_demand, I):
        r.append(val)

    r.append("\n")
    for val in np.random.normal(mean_cost, stdev_cost, J):
       r.append(val)

    r.append("\n")
    for val in np.random.normal(mean_capacity, stdev_capacity, J):
       r.append(val)

    demand_pos_x = np.random.normal(0, stdev_demand_x, I)
    demand_pos_y = np.random.normal(0, stdev_demand_y, I)
    facility_pos_x = np.random.normal(0, stdev_facility_x, J)
    facility_pos_y = np.random.normal(0, stdev_facility_y, J)

    r.append("\n")

    for i in range(I):
        for j in range(J):
            r.append(math.sqrt(math.pow(demand_pos_x[i] - facility_pos_x[j], 2) + math.pow(demand_pos_y[i] + facility_pos_y[j], 2)))

    r.append("\n")
    res[0] = " ".join([str(x) for x in r])
    res[1] = [demand_pos_x, demand_pos_y]
    res[2] = [facility_pos_x, facility_pos_y]

#    for i in range(I):
#            print(demand_pos_x[i], demand_pos_y[i])
#    for j in range(J):
#            print(facility_pos_x[j], facility_pos_y[j])

def genDataByStringVar(l):
    r = []
    for i in l:
        r.append(i.get())
    return genData(*r)

def plotData():
    plt.clf()
    plt.subplot()
    plt.scatter(res[0][0],res[0][1], marker="x")
    plt.scatter(res[1][0],res[1][1], marker="o")
    plt.show()

def displayData():
    box = Tk()
    box.title("Result")
    w = Text(box)
    w.insert(INSERT, res[0])
    b = Button(box, text="Close", command=box.destroy)
    p = Button(box, text="Plot", command= lambda: plotData())
    w.pack()
    b.pack()
    p.pack()
    box.mainloop()

def clickGen(l):
    res = genDataByStringVar(l)
    displayData()

win = Tk()

paraUiText = [
    "Number of Demand Points", # I
    "Number of Facilities", # J
    "Mean of Demand Value",
    "Mean of Delivery Cost",
    "Mean of Facility Capacity",
    "Stdev of Demand Value",
    "Stdev of Delivery Cost",
    "Stdev of Facility Capacity",
    "Stdev of x-coor for Demand Points",
    "Stdev of y-coor for Demand Points",
    "Stdev of x-coor for Facility",
    "Stdev of y-coor for Facility"
]

paraStringVar = []
labelUi = []
entryUi = []

for i in range(len(paraUiText)):
    paraStringVar.append(StringVar())
    labelUi.append(Label(text = paraUiText[i]))
    entryUi.append(Entry(win, textvariable=paraStringVar[i]))

win.title("Facility Siting Test Data Generator")
label = Label(win, text = "Facility Siting Test Data Generator")
label.grid(column=0,row=0)

for i in range(len(paraUiText)):
    paraStringVar[i].set(8)
    labelUi[i].grid(column = 0, row = i + 1)
    entryUi[i].grid(column = 1, row = i + 1)

button = Button(win, text="Generate", command= lambda: clickGen(paraStringVar))
button.grid(column = 0, row = len(paraUiText) + 1)

win.mainloop()
