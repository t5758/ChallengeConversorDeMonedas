import java.util.Map;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        ServicioDeCambio servicio = new ServicioDeCambio("fdaf095f54afdd771beb5510");

        try {
            Map<String, String> monedas = servicio.obtenerListadoMonedas();
            System.out.println("=== Monedas disponibles ===");
            for (Map.Entry<String, String> moneda : monedas.entrySet()) {
                System.out.println(moneda.getKey() + " - " + moneda.getValue());
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener el listado de monedas.");
        }

        while (true) {
            System.out.println("=== *** Conversor de Monedas *** ===");

            System.out.print("Moneda que tienes (ej: USD): ");
            String monedaOrigen = entrada.nextLine().toUpperCase();

            System.out.print("Moneda a la que quieres convertir (ej: COP): ");
            String monedaDestino = entrada.nextLine().toUpperCase();

            System.out.print("Cantidad que quieres convertir - VALOR: ");
            double cantidad = entrada.nextDouble();
            entrada.nextLine();

            try {
                double tasa = servicio.obtenerTasa(monedaOrigen, monedaDestino);
                double resultado = cantidad * tasa;

                System.out.printf("%.2f %s equivale a %.2f %s%n", cantidad, monedaOrigen, resultado, monedaDestino);
            } catch (Exception error) {
                System.out.println("Ups... algo falló: " + error.getMessage());
            }

            System.out.println("¿Qué deseas hacer ahora?");
            System.out.println("1. Hacer otra conversión");
            System.out.println("2. Salir");

            String opcion = entrada.nextLine();

            if (opcion.equals("2")) {
                System.out.println("¡Hasta pronto!");
                break;
            }
        }

        entrada.close();
    }
}

