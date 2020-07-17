#/bin/bash
gnuplot <<- EOF
	set style line 1 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 2 # --- green
	set style line 2 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 2 # --- red

	set style line 11 lc rgb '#808080' lt 1
	set border 3 back ls 11
	set tics nomirror

	set style line 12 lc rgb '#808080' lt 0 lw 1
	set grid back ls 12

	set key vertical
	set key bottom

	set ylabel 'Cantidad de Beneficiarios'
	set xlabel 'Año'
	set title 'Beneficiarios de la Ley 22'
	
	set output 'plot.png'

	plot 'beneficiaries_by_year.csv' u 1:2 t 'Sin Categorizar' w lp ls 1, 'beneficiaries_by_year.csv' u 1:3 t 'Categoría de Tecnología' w lp ls 2
EOF

