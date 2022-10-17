package main

import (
	"fmt"
	"math/rand"
	"time"
)

var nDest, nRunWays = 6, 3
var sem = nRunWays

func plane(passengers chan int) {
	arriveTime := time.Now()
	freePlaces := rand.Intn(300) + 201
	dest := rand.Intn(nDest)
	for arriveTime.UnixMilli()-time.Now().UnixMilli() < 10 {
		ps := <-passengers
		if ps == dest {
			freePlaces--
			if freePlaces == 0 {
				break
			}
		} else {
			passengers <- ps
		}
	}
	b := true
	for sem == 0 {
		if b {
			fmt.Println("Усі взлітні смуги зайняті")
			b = false
		}
	}
	sem--
	time.Sleep(100 * time.Millisecond)
	sem++
	fmt.Println("Звільнилася взлітна смуга")
	fmt.Printf("Літак вилетів у напрямку %d\n", dest)
}

func main() {
	passengers := make(chan int, 2000)
	for {
		for i := 0; i < rand.Intn(500); i++ {
			if i%100 != 0 {
				passengers <- rand.Int() % nDest
			}
		}
		go plane(passengers)
	}
}
