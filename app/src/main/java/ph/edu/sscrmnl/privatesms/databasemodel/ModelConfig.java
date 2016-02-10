package ph.edu.sscrmnl.privatesms.databasemodel;

/**
 * Created by IDcLxViI on 2/7/2016.
 */
public class ModelConfig {

        // data structure
        private String pin = null;

        public enum fields{
            Pin
        }


        public String[] getFields(){
            String[] f = new String[fields.values().length];
            for(int x=0; x < f.length; x++ ){
                f[x] = fields.values()[x].toString();

            }
            return f;
        }

        public ModelConfig() {

        }

        public ModelConfig(String PIN) {
            this.pin = PIN;
        }



        // setters
        public void setPin(String PIN) {
            this.pin = PIN;
        }

        // getters
        public String getPin() {
            return this.pin;
        }


}
