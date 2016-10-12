import matplotlib.pyplot as plt

colors = ['b', 'g', 'r', 'c', 'm', 'y', 'k']
n = 0

while True:
    try:
      x = [float(i) for i in input().split(" ") if i.strip()]
      y = [float(i) for i in input().split(" ") if i.strip()]
    except EOFError:
        break
    for i in range(1, len(x)):
        plt.plot([x[0], x[i]],[y[0], y[i]], color=colors[n % len(colors)])
    plt.scatter(x[0], y[0], marker="x")
    print([x[0], y[0]])
    n += 1

plt.show()
