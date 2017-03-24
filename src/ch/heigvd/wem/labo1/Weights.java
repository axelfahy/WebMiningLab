package ch.heigvd.wem.labo1;

import java.io.Serializable;

public class Weights implements Serializable {

    //private static final long serialVersionUID = -7032327683456713025L;

    private Long frequency;
    private Double weightNorm;
    private Double weightTfIdf;

    public Weights() {

    }

    public Long getFrequency() {
        return frequency;
    }

    public Double getWeightNorm() {
        return weightNorm;
    }

    public Double getWeightTfIdf() {
        return weightTfIdf;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public void setWeightNorm(Double weightNorm) {
        this.weightNorm = weightNorm;
    }

    public void setWeightTfIdf(Double weightTfIdf) {
        this.weightTfIdf = weightTfIdf;
    }
}
