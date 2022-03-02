% Metoda prosté iterace
function [xn, n] = itrace(g, x0, epsilon)

n = 0;
x = x0;
% x = g(x) - pevný bod
xn = g(x);

while (abs(x - xn) && n < 250)
    n = n + 1;
    % dlaší krok
    x = xn;
    xn = g(x);
end