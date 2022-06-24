module com.komar_olga.cloud {

    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.codec;
    requires com.komar_olga.cloud.model;



    opens com.komar_olga.cloud to javafx.fxml;
    exports com.komar_olga.cloud;
}