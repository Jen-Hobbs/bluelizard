package io.transmogrifier;


import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSValue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Transmogrifier<T>
{
    private final File datasetRoot;
    private final File converterRoot;
    private TransmogrifierDelegate<T> delegate;

    {
        delegate = new NullTransmogrifierDelegate();
    }

    public Transmogrifier(final File dir)
        throws IOException
    {
        boolean result;

        if(dir == null)
        {
            throw new IllegalArgumentException("dir cannot be null");
        }

        if(!(dir.exists()))
        {
            throw new IllegalArgumentException(String.format("\"%s\" must exist", dir.getAbsolutePath()));
        }

        if(!(dir.isDirectory()))
        {
            throw new IllegalArgumentException(String.format("\"%s\" must be a directory", dir.getAbsolutePath()));
        }

        if(!(dir.canWrite()))
        {
            throw new IllegalArgumentException(String.format("\"%s\" must be writable", dir.getAbsolutePath()));
        }

        datasetRoot = new File(dir, "datasets");
        result = datasetRoot.mkdirs();

        if(!(result))
        {
            if(!(datasetRoot.exists()))
            {
                throw new IOException(String.format("cannot create \"%s\"", datasetRoot.getAbsolutePath()));
            }
        }

        converterRoot = new File(dir, "converters");
        result = converterRoot.mkdirs();

        if(!(result))
        {
            if(!(converterRoot.exists()))
            {
                throw new IOException(String.format("cannot create \"%s\"", converterRoot.getAbsolutePath()));
            }
        }
    }

    public void processManifest(final T                     id,
                                final ManifestDownloader<T> manifestDownloader,
                                final DatasetDownloader<T> downloader)
    {
        final Manifest manifest;

        delegate.didStartManifestDownload(id);

        try
        {
            manifest = manifestDownloader.downloadManifest(id);
            delegate.didFinishManifestDownload(id);
            processManifest(id, manifest, downloader);
        }
        catch(final ManifestDownloadException ex)
        {
            delegate.didFailManifestDownload(id, ex);
        }
    }

    public void processManifest(final T                    id,
                                final Manifest manifest,
                                final DatasetDownloader<T> downloader)
    {
        final List<Manifest.Dataset>                   datasetsToProcess;
        final Map<Manifest.Dataset, File>              downloadedConverters;
        final Map<Manifest.Dataset, File[]>            downloadedDatasets;
        final List<Manifest.Dataset>                   failedDatasets;
        final DatasetDownloader.DatasetDownloadHandler handler;
        final Manifest.Dataset[]                       datasets;

        if(id == null)
        {
            throw new IllegalArgumentException("id cannot be null");
        }

        if(manifest == null)
        {
            throw new IllegalArgumentException("manifest cannot be null");
        }

        datasetsToProcess    = Collections.synchronizedList(new ArrayList<>());
        downloadedConverters = Collections.synchronizedMap(new HashMap<>());
        downloadedDatasets   = Collections.synchronizedMap(new HashMap<>());
        failedDatasets       = Collections.synchronizedList(new ArrayList<>());
        handler = new DatasetDownloader.DatasetDownloadHandler()
        {
            @Override
            public void converterDownloadStarted(Manifest.Dataset dataset)
            {
            }

            @Override
            public void converterDownloadSuccess(Manifest.Dataset dataset)
            {
            }

            @Override
            public void converterDownloadFailed(Manifest.Dataset dataset, Throwable ex)
            {
            }

            @Override
            public void datasetDownloadStarted(final Manifest.Dataset dataset,
                                               final Manifest.Dataset.Download download)
            {
                delegate.didStartDatasetDownload(id, dataset, download);
            }

            @Override
            public void datasetDownloadSuccess(final Manifest.Dataset dataset,
                                               final Manifest.Dataset.Download download)
            {
                delegate.didFinishDatasetDownload(id, dataset, download);
            }

            @Override
            public void datasetDownloadFailed(final Manifest.Dataset dataset,
                                              final Manifest.Dataset.Download download,
                                              final Throwable ex)
            {
                delegate.didFailDatasetDownload(id, dataset, download, ex);
            }

            @Override
            public void datasetDownloadSuccess(final Manifest.Dataset dataset,
                                               final File             converterFile,
                                               final File[]           files)
            {
                delegate.didFinishDatasetDownloads(id, dataset);

                synchronized(datasetsToProcess)
                {
                    datasetsToProcess.remove(dataset);

                    if(converterFile != null)
                    {
                        downloadedConverters.put(dataset, converterFile);
                    }

                    downloadedDatasets.put(dataset, files);
                }

                if(datasetsToProcess.isEmpty())
                {
                    delegate.didFinishDatasetsDownload(id);
                    processDatasets(id, downloadedConverters, downloadedDatasets);
                }
            }

            @Override
            public void datasetDownloadFailed(final Manifest.Dataset dataset,
                                              final Throwable        ex)
            {
                synchronized(datasetsToProcess)
                {
                    datasetsToProcess.remove(dataset);
                    failedDatasets.add(dataset);
                }

                if(datasetsToProcess.isEmpty())
                {
                    delegate.didFinishDatasetsDownload(id);
                    processDatasets(id, downloadedConverters, downloadedDatasets);
                }
            }
        };

        datasets = manifest.getDatasets();
        delegate.didStartManifestProcessing(id);
        delegate.didStartDatasetsDownload(id);

        // Yes, have to do both loops.
        for(final Manifest.Dataset dataset : datasets)
        {
            if(delegate.shouldDownloadDataset(id, dataset))
            {
                datasetsToProcess.add(dataset);
            }
        }

        for(final Manifest.Dataset dataset : datasetsToProcess)
        {
            downloadDatasets(id,
                             dataset,
                             downloader,
                             handler);
        }
    }

    private void downloadDatasets(final T                                  id,
                                  final Manifest.Dataset                   dataset,
                                  final DatasetDownloader<T> downloader,
                                  DatasetDownloader.DatasetDownloadHandler handler)
    {
        if(dataset == null)
        {
            throw new IllegalArgumentException("dataset cannot be null");
        }

        delegate.didStartDatasetDownloads(id, dataset);
        downloader.download(id,
                            dataset,
                            handler);
    }

    private void processDatasets(final T                             id,
                                 final Map<Manifest.Dataset, File>   converters,
                                 final Map<Manifest.Dataset, File[]> datasets)
    {
        final Map<Manifest.Dataset, Pair<File[], Boolean>> results;

        results = new HashMap<>();
        delegate.didStartDatasetsConversion(id);

        for(final Map.Entry<Manifest.Dataset, File[]> entry : datasets.entrySet())
        {
            final Manifest.Dataset dataset;
            final File[]           downloadedFiles;
            final File             downloadedConverterFile;
            final File[]           newFiles;

            dataset                 = entry.getKey();
            downloadedFiles         = entry.getValue();
            downloadedConverterFile = converters.get(dataset);

            try
            {
                newFiles = moveDownloadedDataset(id,
                                                 dataset,
                                                 downloadedFiles);
            }
            catch(final IOException ex)
            {
                ex.printStackTrace();
                continue;
            }

            if (downloadedConverterFile == null)
            {
                final Pair<File[], Boolean> unconvertedData;
                final boolean               conformsToSchema;

                conformsToSchema = dataset.getSchema() != null;

                unconvertedData = renameUnconvertedFiles(dataset,
                                                         newFiles,
                                                         conformsToSchema);
                results.put(dataset, unconvertedData);
            }
            else
            {
                try
                {
                    final File newConverterFile;
                    final Pair<File[], Boolean> convertedData;

                    newConverterFile = moveDownloadedConverter(id,
                            dataset,
                            downloadedConverterFile);
                    convertedData = convert(id, dataset, newConverterFile, newFiles);
                    results.put(dataset, convertedData);
                }
                catch (final IOException ex)
                {
                    final Pair<File[], Boolean> unconvertedData;

                    unconvertedData = renameUnconvertedFiles(dataset, newFiles, false);
                    results.put(dataset, unconvertedData);
                    // TODO: need callback in delegate for fail
                    ex.printStackTrace();
                }
            }
        }

        delegate.didFinishDatasetsConversion(id);
        delegate.didComplete(id, results);
    }

    private Pair<File[], Boolean> convert(final T                id,
                                          final Manifest.Dataset dataset,
                                          final File             converterFile,
                                          final File[]           filesToConvert)
            throws IOException
    {
        final JSContext context;
        final String converterJS;
        final StringBuilder builder;
        final JSValue result;
        final File convertedFile;
        final Pair<File[], Boolean> retVal;

        delegate.didStartDatasetConversion(id, dataset);

        context = new JSContext();
        converterJS = FileUtils.readTextFile(converterFile);
        builder = new StringBuilder();

        for (final File file : filesToConvert)
        {
            final String content;

            content = FileUtils.readTextFile(file);
            builder.append(content);
            builder.append(",");
        }

        builder.setLength(builder.length() - 1);

        context.evaluateScript("var module = {}");
        context.evaluateScript(converterJS);
        context.evaluateScript("var result = convert(" + builder.toString() + ");");
        result = context.property("result");

        final File countryDir;
        final File subdivisionDir;
        final File regionDir;
        final File cityDir;
        final File providerDir;
        final String datasetName;

        countryDir = new File(datasetRoot, dataset.getCountryCode());
        subdivisionDir = new File(countryDir, dataset.getSubdivision());
        regionDir = new File(subdivisionDir, dataset.getRegion());
        cityDir = new File(regionDir, dataset.getCity());
        providerDir = new File(cityDir, dataset.getProvider());
        datasetName = dataset.getName();
        convertedFile = new File(providerDir, datasetName + ".json");

        FileUtils.writeTextFile(result.toString(),
                convertedFile);

        delegate.didFinishDatasetConversion(id, dataset);

        retVal = new Pair<>(new File[] {convertedFile }, Boolean.TRUE);

        return retVal;
    }

    private Pair<File[], Boolean> renameUnconvertedFiles(final Manifest.Dataset dataset,
                                                         final File[] downloadedFiles,
                                                         final Boolean conformsToSchema)
    {
        final Pair<File[], Boolean> results;

        results = new Pair<>(downloadedFiles, conformsToSchema);

        return results;
    }


    private File[] moveDownloadedDataset(final T                id,
                                         final Manifest.Dataset dataset,
                                         final File[]           downloadedFiles)
        throws IOException
    {
        final Manifest.Dataset.Download[] downloads;
        final List<File>                  newFiles;

        downloads = dataset.getDownloads();
        newFiles  = new ArrayList<>();

        for(int i = 0; i < downloads.length; i++)
        {
            final Manifest.Dataset.Download download;
            final File                      downloadedFile;
            final String                    downloadURL;
            final URL                       url;
            final String                    path;
            final int                       index;
            final String                    name;
            final File                      file;

            download       = downloads[i];
            downloadedFile = downloadedFiles[i];
            downloadURL    = download.getSrcURL();

            url   = new URL(downloadURL);
            path  = url.getPath();
            index = path.lastIndexOf('/');

            if(index == -1)
            {
                name = path;
            }
            else
            {
                name = path.substring(index + 1);
            }

            file = move(downloadedFile, dataset, name, datasetRoot);
            newFiles.add(file);
            System.out.println(file.getAbsolutePath());
        }

        delegate.didFinishDatasetConversion(id, dataset);

        return newFiles.toArray(new File[0]);
    }

    private File moveDownloadedConverter(final T                id,
                                         final Manifest.Dataset dataset,
                                         final File             downloadedConveterFile)
        throws IOException
    {
        final String downloadURL;
        final URL    url;
        final String path;
        final int    index;
        final String name;
        final File   file;

        downloadURL = dataset.getConverter();
        url         = new URL(downloadURL);
        path        = url.getPath();
        index       = path.lastIndexOf('/');

        if(index == -1)
        {
            name = path;
        }
        else
        {
            name = path.substring(index + 1);
        }

        file = move(downloadedConveterFile, dataset, name, converterRoot);

        return file;
    }

    private File move(final File             src,
                      final Manifest.Dataset dataset,
                      final String           name,
                      final File             root)
            throws IOException
    {
        final File countryDir;
        final File subdivisionDir;
        final File regionDir;
        final File cityDir;
        final File providerDir;
        final File datasetFile;
        boolean    result;

        countryDir     = new File(root, dataset.getCountryCode());
        subdivisionDir = new File(countryDir, dataset.getSubdivision());
        regionDir      = new File(subdivisionDir, dataset.getRegion());
        cityDir        = new File(regionDir, dataset.getCity());
        providerDir    = new File(cityDir, dataset.getProvider());
        result         = providerDir.mkdirs();

        if(!(result))
        {
            if(!(providerDir.exists()))
            {
                throw new IOException(String.format("cannot create \"%s\"", root.getAbsolutePath()));
            }
        }

        datasetFile = new File(providerDir, dataset.getName() + "-" + name);
        result      = src.renameTo(datasetFile);

        if(!(result))
        {
            throw new IOException(String.format("cannot move \"%s\" to \"%s\"",
                    src.getAbsolutePath(),
                    datasetFile.getAbsolutePath()));
        }

        return datasetFile;
    }

    public void setTransmogrifierDelegate(final TransmogrifierDelegate<T> delegate)
    {
        if(delegate == null)
        {
            this.delegate = new NullTransmogrifierDelegate();
        }
        else
        {
            this.delegate = delegate;
        }
    }

    private class NullTransmogrifierDelegate
        extends AbstractTransmogrifierDelegate<T>
    {
        @Override
        public boolean shouldDownloadDataset(T id, Manifest.Dataset dataset)
        {
            return true;
        }

        @Override
        public void didComplete(T id, Map<Manifest.Dataset, Pair<File[], Boolean>> files)
        {
        }
    }
}
