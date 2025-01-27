package ar.edu.utn.frc.tup.lc.iv.restTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


/**
 * Servicio que se encarga de realizar las peticiones REST a la API de fileManager.
 */
@Service
public class FileManagerClient {

    /**
     * RestTemplate para realizar las peticiones HTTP.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Dirección url donde se levanta el microservicio de file manager.
     */
    @Value("${file.manager.url}")
    private String url;

    /**
     * Sube un archivo al servidor.
     *
     * @param file archivo a subir.
     * @return el UUID del archivo subido.
     */
    public FileClient uploadFile(MultipartFile file) {

        String uploadUrl = url + "/fileManager/savefile";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<FileClient> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                FileClient.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        return response.getBody();
    }

    /**
     * Obtiene un archivo del servidor.
     *
     * @param fileId UUID del archivo a obtener.
     * @return el archivo en formato byte[].
     */
    public byte[] getFile(String fileId) {
        String downloadUrl = url + "/fileManager/getFile/" + fileId;
        System.out.println("Download URL: " + downloadUrl);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        return response.getBody();
    }
}
