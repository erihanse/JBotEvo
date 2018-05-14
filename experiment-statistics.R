homeroute_10robots <- read.table("JBotErihanse/output-homeroute-10robots.txt")
homeroute_15robots <- read.table("JBotErihanse/output-homeroute-15robots.txt")
homeroute_20robots <- read.table("JBotErihanse/output-homeroute-20robots.txt")

random_10robots <- read.table("JBotErihanse/output-random-walk-10robots.txt")
random_15robots <- read.table("JBotErihanse/output-random-walk-15robots.txt")
random_20robots <- read.table("JBotErihanse/output-random-walk-20robots.txt")

plot(homeroute_10robots$V1)
points(homeroute_15robots$V1,col=2)
points(homeroute_20robots$V1,col=3)

boxplot(random_10robots$V1, random_15robots$V1, random_20robots$V1, 
        homeroute_10robots$V1, homeroute_15robots$V1, homeroute_20robots$V1,
        ylab="timesteps",
        names=c("random walk 10 robots", "random walk 15 robots", "random walk 20 robots", 
                "homeroute alg 10 robots","homeroute alg 15 robots","homeroute alg 20 robots"))

