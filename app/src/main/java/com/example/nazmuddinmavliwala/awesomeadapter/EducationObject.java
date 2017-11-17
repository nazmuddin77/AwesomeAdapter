package com.example.nazmuddinmavliwala.awesomeadapter;

/**
 * Created by nazmuddinmavliwala on 18/10/16.
 */
public class EducationObject {

    private final String proficiencyLevel;
    private final String id;
    private final String place;
    private final String institute;
    private final String yearOfPassing;
    private final String stream;
    private final String degree;

    private EducationObject(
            String proficiencyLevel
            , String id
            , String place
            , String institute
            , String yearOfPassing
            , String stream
            , String degree) {
        this.proficiencyLevel = proficiencyLevel;
        this.id = id;
        this.place = place;
        this.institute = institute;
        this.yearOfPassing = yearOfPassing;
        this.stream = stream;
        this.degree = degree;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public String getId() {
        return id;
    }

    public String getPlace() {
        return place;
    }

    public String getInstitute() {
        return institute;
    }

    public String getYearOfPassing() {
        return yearOfPassing;
    }

    public String getStream() {
        return stream;
    }

    public String getDegree() {
        return degree;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String pLevel;
        private String place;
        private String institute;
        private String stream;
        private String degree;
        private String yop;

        public Builder addId(String id) {
            this.id = id;
            return this;
        }

        public Builder addProficiencyLevel(String pLevel) {
            this.pLevel = pLevel;
            return this;
        }

        public Builder addPlace(String place) {
            this.place = place;
            return this;
        }

        public Builder addInstitute(String institute) {
            this.institute = institute;
            return this;
        }

        public Builder addStream(String stream) {
            this.stream = stream;
            return this;
        }

        public Builder addDegree(String degree) {
            this.degree = degree;
            return this;
        }

        public Builder addYearOfPassing(String yop) {
            this.yop = yop;
            return this;
        }

        public EducationObject build() {
            return new EducationObject(
                    pLevel
                    ,id
                    ,place
                    ,institute
                    ,yop
                    ,stream
                    ,degree);
        }
    }
}
