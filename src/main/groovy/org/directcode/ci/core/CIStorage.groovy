package org.directcode.ci.core

import groovy.io.FileType
import groovy.json.JsonBuilder
import jpower.core.Task
import jpower.core.Worker
import org.directcode.ci.utils.Utils

import java.nio.file.Path

class CIStorage {
    private final Map<String, Map<String, Object>> storages = [:]
    private Path storagePath
    private Worker worker = new Worker()
    private boolean autoSave = true

    void load() {
        storagePath.eachFileRecurse(FileType.FILES) { Path path ->
            def file = path.toFile()
            if (!file.name.endsWith(".json")) {
                return
            }
            def storageName = file.name[0..file.name.lastIndexOf('.') - 1]
            storages[storageName] = Utils.parseJSON(path.text) as Map<String, Object>
        }
    }

    protected void start() {
        worker.start()

        addShutdownHook { ->
            save()
        }

        def loadTask = new Task() {
            @Override
            void execute() {
                load()
            }
        }
        def saveTask = new Task() {
            @Override
            void execute() {
                while (autoSave) {
                    save()
                    sleep(4000)
                }
            }
        }
        worker.addTask(loadTask)
        worker.addTask(saveTask)
    }

    void save() {
        storages.each { entry ->
            def storageFile = new File(storagePath.toFile(), "${entry.key}.json").toPath()
            def builder = new JsonBuilder(entry.value)
            storageFile.write(builder.toPrettyString() + System.lineSeparator())
        }
    }

    void setStoragePath(Path path) {
        this.storagePath = path
        path.toFile().mkdirs()
    }

    Path getStoragePath() {
        return storagePath
    }

    Map<String, Object> get(String storageName) {
        if (!(storageName in storages.keySet())) {
            storages[storageName] = [:]
        }
        return storages[storageName]
    }

    Map<String, Map<String, Object>> all() {
        return storages
    }
}
