package pds.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * classe auxilixar que gera códigos alfanuméricos aleatórios
 *
 */
public class GeradorCodigos {

	private static char caracteresPossiveis[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'Z'
	};

	/**
	 * Gera um código alfanumérico aleatório, com o tamanho indicado
	 * 
	 * @param tamanho número de caracteres que terá o código gerado
	 * @return o código aleatório gerado
	 */
	public static String gerarCodigo(int tamanho) {
		String num = "";
		for (int i = 0; i < tamanho; i++)
			num += caracteresPossiveis[ThreadLocalRandom.current().nextInt(caracteresPossiveis.length)];
		return num;
	}
}
