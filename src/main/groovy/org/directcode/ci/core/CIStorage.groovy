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
    private final Worker worker = new Worker()
    private final boolean autoSave = true

    void load() {
        storagePath.eachFileRecurse(FileType.FILES) { Path path ->
            def file = path.toFile()
            if (!file.name.endsWith(".json")) {
                return
            }
            load(file.name[0..file.name.lastIndexOf('.') - 1])
        }
    }

    void load(String storageName) {
        def path = new File(storagePath.toFile(), "${storageName}.json").toPath()
        storages[storageName] = Utils.parseJSON(path.text) as Map<String, Object>
    }

    protected void start() {
        worker.start()

        addShutdownHook { ->
            save()
        }

        worker.addTask(new Task() {
            @Override
            void execute() {
                while (autoSave) {
                    save()
                    sleep(4000)
                }
            }
        })
    }

    void save() {
        storages.keySet().each { storageName ->
            save(storageName)
        }
    }

    synchronized void save(String storageName) {
        def storageFile = new File(storagePath.toFile(), "${storageName}.json").toPath()
        def builder = new JsonBuilder(storages[storageName])
        storageFile.write(builder.toPrettyString() + System.lineSeparator())
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
            if (new File(storagePath.toFile(), "${storageName}.json").exists()) {
                load(storageName)
            } else {
                storages[storageName] = [:]
            }
        }
        return storages[storageName]
    }

    Map<String, Map<String, Object>> all() {
        return storages
    }

    String getJSON(String storageName) {
        def file = new File(storagePath.toFile(), "${storageName}.json")
        if (!file.exists()) {
            return null
        } else {
            return file.text
        }
    }
}
