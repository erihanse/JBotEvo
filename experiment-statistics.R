homeroute_10robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-10robots.txt")
homeroute_15robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-15robots.txt")
homeroute_20robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-20robots.txt")

random_10robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-10robots.txt")
random_15robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-15robots.txt")
random_20robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-20robots.txt")

odneat_10robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-10robots.txt")
odneat_15robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-15robots.txt")
odneat_20robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-20robots.txt")


#plot(homeroute_10robots$V1)
#points(homeroute_15robots$V1,col=2)
#points(homeroute_20robots$V1,col=3)

boxplot(random_10robots$V1, random_15robots$V1, random_20robots$V1,
       homeroute_10robots$V1, homeroute_15robots$V1, homeroute_20robots$V1,
       ylab="timesteps",
       names=c("random walk 10 robots", "random walk 15 robots", "random walk 20 robots",
               "homeroute alg 10 robots","homeroute alg 15 robots","homeroute alg 20 robots"))


#plot(odneat_10robots)
# boxplot(horizontal = 1,odneat_10robots$V1, odneat_15robots$V1, odneat_20robots$V1,
#         ylab="timesteps",
#         names=c("odneat 10 robots", "odneat 15 robots", "odneat 20 robots"))