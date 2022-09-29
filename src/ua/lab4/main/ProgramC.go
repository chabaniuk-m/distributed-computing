package main

func main() {
	g := newSampleGraph()
	wg.Add(6)
	go g.findPath(0, 4)
	go g.changePrice(4, 5, 11)
	go g.deleteAddRoute(3, 4, 2, 5, 18)
	go g.findPath(1, 5)
	go g.deleteAddCity(2)
	go g.findPath(2, 5)
	wg.Wait()
}
