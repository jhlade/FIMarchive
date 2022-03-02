% Newtonova iterační metoda
function [xn, n] = newton(f, df, x0, epsilon)

n = 0;
xk = x0;
xn = x0 - f(x0)/df(x0);

while (abs(xn - xk) > epsilon & n < 250)
	% počítadlo iterací
	n = n+1;

	% aktualizace hodnoty
	xk = xn;
	
	% další člen v řadě
	xn = xk - f(xk)/df(xk);
end


% [xi, n] = newton(@(x) log(x)+(x+1)^3, @(x) (1/x)+3*(x+1)^2, 0.5, 10e-6)