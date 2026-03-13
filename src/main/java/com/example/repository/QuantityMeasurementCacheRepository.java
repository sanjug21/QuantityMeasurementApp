package com.example.repository;

import com.example.entity.QuantityMeasurementEntity;
import com.example.exception.QuantityMeasurementException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {
    private static final Path DEFAULT_STORAGE_PATH = Paths.get(
            System.getProperty("java.io.tmpdir"),
            "quantity-measurement-app",
            "quantity-measurement-cache.ser");

    private static volatile QuantityMeasurementCacheRepository instance;

    private final List<QuantityMeasurementEntity> cache;
    private final Path storagePath;

    private QuantityMeasurementCacheRepository() {
        this(DEFAULT_STORAGE_PATH);
    }

    private QuantityMeasurementCacheRepository(Path storagePath) {
        this.storagePath = Objects.requireNonNull(storagePath, "Storage path must not be null.");
        this.cache = new ArrayList<QuantityMeasurementEntity>();
        initializeStorage();
        loadFromDisk();
    }

    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementCacheRepository.class) {
                if (instance == null) {
                    instance = new QuantityMeasurementCacheRepository();
                }
            }
        }
        return instance;
    }

    private void initializeStorage() {
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            throw new QuantityMeasurementException("Unable to initialize repository storage.", exception);
        }
    }

    @Override
    public synchronized QuantityMeasurementEntity save(QuantityMeasurementEntity entity) {
        Objects.requireNonNull(entity, "QuantityMeasurementEntity must not be null.");
        writeToDisk(entity);
        cache.add(entity);
        return entity;
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> findAll() {
        return new ArrayList<QuantityMeasurementEntity>(cache);
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> getAllMeasurements() {
        return findAll();
    }

    @Override
    public synchronized Optional<QuantityMeasurementEntity> findById(String id) {
        for (QuantityMeasurementEntity entity : cache) {
            if (entity.getId().equals(id)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized void clear() {
        cache.clear();
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException exception) {
            throw new QuantityMeasurementException("Unable to clear quantity measurement cache.", exception);
        }
    }

    private synchronized void loadFromDisk() {
        if (!Files.exists(storagePath)) {
            return;
        }

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(storagePath));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            while (true) {
                Object storedObject = objectInputStream.readObject();
                if (storedObject instanceof QuantityMeasurementEntity) {
                    cache.add((QuantityMeasurementEntity) storedObject);
                }
            }
        } catch (EOFException exception) {
            return;
        } catch (IOException exception) {
            throw new QuantityMeasurementException("Unable to load quantity measurement cache from disk.", exception);
        } catch (ClassNotFoundException exception) {
            throw new QuantityMeasurementException("Unable to deserialize quantity measurement cache.", exception);
        }
    }

    private void writeToDisk(QuantityMeasurementEntity entity) {
        boolean append = false;
        try {
            append = Files.exists(storagePath) && Files.size(storagePath) > 0;
        } catch (IOException exception) {
            throw new QuantityMeasurementException("Unable to inspect repository storage.", exception);
        }

        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(
                storagePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND));
                ObjectOutputStream objectOutputStream = append
                        ? new AppendableObjectOutputStream(outputStream)
                        : new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(entity);
            objectOutputStream.flush();
        } catch (IOException exception) {
            throw new QuantityMeasurementException("Unable to persist quantity measurement entity.", exception);
        }
    }

    private static class AppendableObjectOutputStream extends ObjectOutputStream {
        AppendableObjectOutputStream(OutputStream outputStream) throws IOException {
            super(outputStream);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }
}