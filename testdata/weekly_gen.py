import sys
import numpy as np
import math
from tkinter import *
import matplotlib
matplotlib.use("TkAgg")
from matplotlib import pyplot as plt

res = [0,0]

def genData(I, J, mean_job, mean_capacity, mean_reward, mean_penalty, stdev_job, stdev_capacity, stdev_reward, stdev_penalty, stdev_job_x, stdev_job_y):

    I = int(I)
    J = int(J)
    mean_job = float(mean_job)
    mean_capacity = float(mean_capacity)
    mean_reward = float(mean_reward)
    mean_penalty = float(mean_penalty)
    stdev_job = float(stdev_job)
    stdev_capacity = float(stdev_capacity)
    stdev_reward = float(stdev_reward)
    stdev_penalty = float(stdev_penalty)
    stdev_job_x = float(stdev_job_x)
    stdev_job_y = float(stdev_job_y)

    r = []

    r.append(I)
    r.append(J)

    r.append("\n")
    for val in np.random.normal(mean_job, stdev_job, I):
        r.append(val)

    r.append("\n")
    for val in np.random.normal(mean_capacity, stdev_capacity, J):
        r.append(val)

    r.append("\n")
    for i in range(I):
        for j in range(J):
            r.append(J - j)
        r.append("\n")

    r.append("\n")
    for val in np.random.normal(mean_penalty, stdev_penalty, I):
        r.append(val)

# 任務 k 最多可以被分成幾天完成

    job_pos_x = np.random.normal(0, stdev_job_x, I)
    job_pos_y = np.random.normal(0, stdev_job_y, I)

    r.append("\n")

    for i1 in range(I):
        for i2 in range(i1 + 1, I):
            r.append(math.sqrt(math.pow(job_pos_x[i1] - job_pos_x[i2], 2) + math.pow(job_pos_y[i1] - job_pos_y[i2], 2)))

    r.append("\n")
    res[0] = " ".join([str(x) for x in r])
    res[1] = [job_pos_x, job_pos_y]

    return res

#    for i in range(I):
#        print(job_pos_x[i], job_pos_y[i])
#    for j in range(J):
#        print(facility_pos_x[j], facility_pos_y[j])

def genDataByStringVar(l):
    r = []
    for i in l:
        r.append(i.get())
    return genData(*r)

def plotData():
    plt.clf()
    plt.subplot()
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

def main():
    win = Tk()

    paraUiText = [
        "Number of Jobs", # I
        "Number of Workdays", # J
        "Mean of Job Handling Time",
        "Mean of Work Capacity",
        "Mean of Reward",
        "Mean of Penalty",
        "Stdev of Job Handling Time",
        "Stdev of Work Capacity",
        "Stdev of Reward",
        "Stdev of Penalty",
        "Stdev of x-coor for Job Points",
        "Stdev of y-coor for Job Points"
    ]

    paraStringVar = []
    labelUi = []
    entryUi = []

    for i in range(len(paraUiText)):
        paraStringVar.append(StringVar())
        labelUi.append(Label(text = paraUiText[i]))
        entryUi.append(Entry(win, textvariable=paraStringVar[i]))

    win.title("Weekly Job Scheduling Test Data Generator")
    label = Label(win, text = "Weekly Job Scheduling Test Data Generator")
    label.grid(column=0,row=0)

    for i in range(len(paraUiText)):
        paraStringVar[i].set(8)
        labelUi[i].grid(column = 0, row = i + 1)
        entryUi[i].grid(column = 1, row = i + 1)

    button = Button(win, text="Generate", command= lambda: clickGen(paraStringVar))
    button.grid(column = 0, row = len(paraUiText) + 1)

    win.mainloop()

if __name__ == "__main__":
    main()