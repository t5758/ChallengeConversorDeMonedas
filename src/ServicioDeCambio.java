import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ServicioDeCambio {
    private final String claveApi;
    private final Gson gson = new Gson();

    public ServicioDeCambio(String claveApi) {
        this.claveApi = claveApi;
    }

    public double obtenerTasa(String origen, String destino) throws Exception {
        String urlTexto = "https://v6.exchangerate-api.com/v6/" + claveApi + "/pair/" + origen + "/" + destino;
        URL url = new URL(urlTexto);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        int respuesta = conexion.getResponseCode();
        if (respuesta == 404) {
            throw new RuntimeException("Revisa si escribiste bien el código de la moneda (como USD, COP, EUR, etc). No se encontró el recurso.");
        } else if (respuesta != 200) {
            throw new RuntimeException("Error al conectar: " + respuesta);
        }

        BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        StringBuilder textoRespuesta = new StringBuilder();
        String linea;
        while ((linea = lector.readLine()) != null) {
            textoRespuesta.append(linea);
        }
        lector.close();

        JsonObject json = gson.fromJson(textoRespuesta.toString(), JsonObject.class);
        if (!json.get("result").getAsString().equals("success")) {
            throw new RuntimeException("Fallo de la API: " + json.get("error-type").getAsString());
        }

        return json.get("conversion_rate").getAsDouble();
    }

    public Map<String, String> obtenerListadoMonedas() throws Exception {
        String urlTexto = "https://v6.exchangerate-api.com/v6/" + claveApi + "/codes";
        URL url = new URL(urlTexto);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        int respuesta = conexion.getResponseCode();
        if (respuesta == 404) {
            throw new RuntimeException("No se encontró el recurso. Verifica la clave API o la URL.");
        } else if (respuesta != 200) {
            throw new RuntimeException("Error al obtener monedas: " + respuesta);
        }

        BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        StringBuilder textoRespuesta = new StringBuilder();
        String linea;
        while ((linea = lector.readLine()) != null) {
            textoRespuesta.append(linea);
        }
        lector.close();

        JsonObject json = gson.fromJson(textoRespuesta.toString(), JsonObject.class);
        if (!json.get("result").getAsString().equals("success")) {
            throw new RuntimeException("Error en el listado de monedas: " + json.get("error-type").getAsString());
        }

        Map<String, String> monedas = new HashMap<>();
        for (var elemento : json.getAsJsonArray("supported_codes")) {
            String codigo = elemento.getAsJsonArray().get(0).getAsString();
            String nombre = elemento.getAsJsonArray().get(1).getAsString();
            monedas.put(codigo, nombre);
        }

        List<Map.Entry<String, String>> listaOrdenada = new ArrayList<>(monedas.entrySet());
        listaOrdenada.sort(Comparator.comparing(Map.Entry::getValue));

        Map<String, String> monedasOrdenadas = new LinkedHashMap<>();
        for (Map.Entry<String, String> entrada : listaOrdenada) {
            monedasOrdenadas.put(entrada.getKey(), entrada.getValue());
        }

        return monedasOrdenadas;
    }
}


