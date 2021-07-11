package hr.bskracic.squil.dto.container;

import com.github.dockerjava.api.model.Container;

public class ContainerDTO {
    private String id;
    private String status;
    private String image;
    private String name;

    public ContainerDTO(Container container) {
        this.id = container.getId();
        this.status = container.getStatus();
        this.image = container.getImage();
        if(container.getNames().length > 0) {
            this.name = container.getNames()[0];
        } else {
            this.name = "";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
