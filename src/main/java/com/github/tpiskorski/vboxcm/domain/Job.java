package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.Objects;

public class Job {

    private StringProperty jobName = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty progress = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> startTime = new SimpleObjectProperty<>();

    static Callback<Job, Observable[]> extractor() {
        return (Job job) -> new Observable[]{job.jobNameProperty(), job.statusProperty(), job.progressProperty(), job.startTimeProperty()};
    }


    public String getJobName() {
        return jobName.get();
    }

    public void setJobName(String jobName) {
        this.jobName.set(jobName);
    }

    public StringProperty jobNameProperty() {
        return jobName;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getProgress() {
        return progress.get();
    }

    public void setProgress(String progress) {
        this.progress.set(progress);
    }

    public StringProperty progressProperty() {
        return progress;
    }

    public LocalDateTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime.set(startTime);
    }

    public ObjectProperty<LocalDateTime> startTimeProperty() {
        return startTime;
    }

    @Override public int hashCode() {
        return Objects.hash(jobName.get().hashCode(), startTime.hashCode());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Job)) {
            return false;
        }
        Job that = (Job) obj;

        return Objects.equals(this.jobName.get(), that.jobName.get()) && Objects.equals(this.startTime, that.startTime);

    }
}
