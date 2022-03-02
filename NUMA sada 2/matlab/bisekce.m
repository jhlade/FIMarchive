% Obecná bisekce
function m = bisekce(funkce, hledana_hodnota, a_start, b_start, epsilon)

% počáteční odhad
m = (a_start + b_start) / 2.0;

% výpočet funkce
x = funkce(m);

if (abs(x - hledana_hodnota) >= epsilon)

	if (x - hledana_hodnota < 0)
		a = m;
		b = b_start;
	else 
		a = a_start;
		b = m;
	end

	m = bisekce(funkce, hledana_hodnota, a, b, epsilon);
end
