package tpiskorski.machinator.flow.quartz.backup;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.model.job.JobStatus;
import tpiskorski.machinator.model.job.JobType;

import java.time.LocalDateTime;

@Component
public class BackupJobListener implements JobListener {

    private static final String LISTENER_NAME = "BackupJobListener";
    @Autowired private JobService jobService;

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("backups")) {

            JobDataMap map = context.getMergedJobDataMap();
            BackupDefinition backupDefinition = (BackupDefinition) map.get("backupDefinition");

            Job job = new Job(key.getName());

            job.setJobType(JobType.BACKUP);
            job.setDescription(backupDefinition.id());
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getGroup().equals("backups")) {

            System.out.println("jobExecutionVetoed");
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("backups")) {

            Job job = jobService.getLast(key.getName());
            job.setEndTime(LocalDateTime.now());
            if (jobException == null) {
                job.setStatus(JobStatus.COMPLETED);
            } else {
                job.setStatus(JobStatus.FAILED);
                String unwrappedMessage = ((SchedulerException) jobException.getCause()).getUnderlyingException().getMessage();
                job.setErrorCause(unwrappedMessage);
            }
        }
    }

    private boolean isBackupJob(JobExecutionContext context) {
        return context.getJobDetail().getJobClass().equals(BackupJob.class);
    }
}
