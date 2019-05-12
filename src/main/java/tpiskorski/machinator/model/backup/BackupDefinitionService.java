package tpiskorski.machinator.model.backup;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.backup.BackupScheduler;

@Service
public class BackupDefinitionService {

    private final BackupDefinitionRepository backupDefinitionRepository;
    private final BackupScheduler backupScheduler;

    @Autowired
    public BackupDefinitionService(BackupDefinitionRepository backupDefinitionRepository, BackupScheduler backupScheduler) {
        this.backupDefinitionRepository = backupDefinitionRepository;
        this.backupScheduler = backupScheduler;
    }

    public ObservableList<BackupDefinition> getBackups() {
        return backupDefinitionRepository.getBackups();
    }

    public void add(BackupDefinition backupDefinition) {
        backupDefinitionRepository.add(backupDefinition);
    }

    public void remove(BackupDefinition backupDefinition) {
        backupDefinitionRepository.remove(backupDefinition);
    }

    public void deactivate(BackupDefinition backupToDeactivate) {
        backupScheduler.removeTaskFromScheduler(backupToDeactivate);
        backupToDeactivate.setActive(false);
    }

    public void activate(BackupDefinition backupToActivate) {
        backupScheduler.addTaskToScheduler(backupToActivate);
        backupToActivate.setActive(true);
    }

    public void triggerNow(BackupDefinition backupDefinitionToTrigger) {
        backupScheduler.triggerNow(backupDefinitionToTrigger);
    }
}