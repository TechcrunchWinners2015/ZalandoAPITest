public class NeximoFunctions{

    public static void notifyAssociate(String department){
        sendSMS("A customer needs your assistance in the " + department + " department.", "14843402534");
    }

    public static void sendSMS(String message, String phone) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("https://rest.nexmo.com/sms/json?api_key=77924740&api_secret=9204e775&from=12132633774&to=" + phone + "&text=" + message);
            httpGet.setURI(uri);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();

        } catch (Exception e) {
            // TODO: handle exception
        } 
    }

}