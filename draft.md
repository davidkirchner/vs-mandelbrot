# Quickdraft

1. Client schickt 2 Servers die Koordinate (x,y).
2. Der erste Server berechnet den RGB-Wert für alle Punkte der linken Seite, der zweite Server für alle Punkte der rechte Seite. Beide Servers antworten den Client mit einem int[] array x1y1RGB1x2y2RGB2...
```
Color c = new Color(0, 0, 204);
int sRGB = c.getRGB();
```
3. Client bekommt Response von Server und verwendet Thread zu "malen", da es ist möglich, dass Servers zu viele Pakten schicken und Client keine Zeit haben um mit den Antworten zu arbeiten.
