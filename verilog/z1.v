module z1(
			input wire awef,
			input wire gea,
			output wire eag);



	wire pin;


	nor (pin, awef, gea);
	and (eag, pin, null);


endmodule