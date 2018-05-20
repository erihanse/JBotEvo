homeroute_10robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-10robots.txt")
homeroute_15robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-15robots.txt")
homeroute_20robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-homeroute-20robots.txt")

random_10robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-10robots.txt")
random_15robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-15robots.txt")
random_20robots <- read.table("JBotErihanse/confs/output-random-and-homeroutealg-autom/output-random-walk-20robots.txt")

odneat_10robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-10robots.txt")
odneat_15robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-15robots.txt")
odneat_20robots <- read.table("JBotErihanse/confs/output-odneat-autom/output-odneat-20robots.txt")


boxplot(random_10robots$V1, random_15robots$V1, random_20robots$V1,
  homeroute_10robots$V1, homeroute_15robots$V1, homeroute_20robots$V1,
  odneat_10robots$V1, odneat_15robots$V1, odneat_20robots$V1,
  xlab="Timesteps",
  names=c("random walk 10 robots", "random walk 15 robots", "random walk 20 robots",
          "homeroute alg 10 robots","homeroute alg 15 robots","homeroute alg 20 robots",
          "odneat 10 robots", "odneat 15 robots", "odneat 20 robots"), 
          horizontal = 1, axes = FALSE, col=c('powderblue', 'mistyrose', 'gold'))
axis(1, seq(0,10000, 500), seq(0, 10000, 500),las=2)
axis(2, c(1,2,3,4,5,6,7,8,9), c("Random walk 10 robots","Random walk 15 robots","Random walk 20 robots",
                          "Pre-programmed 10 robots","Pre-programmed 15 robots","Pre-programmed 20 robots",
                          "odNEAT 10 robots","odNEAT 15 robots","odNEAT 20 robots"),las=2,par(mar=c(5,15,1,1)))

distance_travelled_boxplot <- boxplot(random_10robots$V2, random_15robots$V2, random_20robots$V2,
                                      homeroute_10robots$V2, homeroute_15robots$V2, homeroute_20robots$V2,
                                      odneat_10robots$V2, odneat_15robots$V2, odneat_20robots$V2,
                                      xlab="Total Distance Travelled",
                                      names=c("random walk 10 robots", "random walk 15 robots", "random walk 20 robots",
                                              "homeroute alg 10 robots","homeroute alg 15 robots","homeroute alg 20 robots",
                                              "odneat 10 robots", "odneat 15 robots", "odneat 20 robots"), 
                                      horizontal = 1, axes = FALSE, col=c('powderblue', 'mistyrose', 'gold'))
axis(1, seq(0,10000, 100), seq(0, 10000, 100),las=2)
axis(2, c(1,2,3,4,5,6,7,8,9), c("Random walk 10 robots","Random walk 15 robots","Random walk 20 robots",
                                "Pre-programmed 10 robots","Pre-programmed 15 robots","Pre-programmed 20 robots",
                                "odNEAT 10 robots","odNEAT 15 robots","odNEAT 20 robots"),las=2,par(mar=c(5,15,1,1)))
