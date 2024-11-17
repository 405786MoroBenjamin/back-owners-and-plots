package ar.edu.utn.frc.tup.lc.iv.services.dashboard;

import ar.edu.utn.frc.tup.lc.iv.dtos.dashboard.*;
import ar.edu.utn.frc.tup.lc.iv.entities.PlotEntity;

import ar.edu.utn.frc.tup.lc.iv.entities.OwnerEntity;

import ar.edu.utn.frc.tup.lc.iv.entities.PlotOwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.OwnerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotOwnerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.PlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;

import java.time.format.TextStyle;

import java.util.stream.Collectors;

/**
 * Servicio que maneja las estadísticas relacionadas con los propietarios y los lotes.
 */
@Service
@RequiredArgsConstructor
public class OwnerStatsService implements OwnerStatsInterface {

    /**
     * Repositorio para manejar los datos de los propietarios.
     */
    private final OwnerRepository ownerRepository;

    /**
     * Repositorio para manejar los datos de los lotes.
     */
    private final PlotRepository plotRepository;

    /**
     * Repositorio para manejar la relación entre propietarios y lotes.
     */
    private final PlotOwnerRepository plotOwnerRepository;

    /**
     * Obtiene una lista de datos de las manzanas.
     *
     * @return una lista de datos de las manzanas
     */
    @Override
    public List<BlockData> getBlocksData(LocalDate startDate, LocalDate endDate) {
        List<PlotEntity> plots = this.getFiltersPlots(startDate, endDate, null, null);
        Map<Integer, BlockData> blockDataMap = plots.stream()
                .collect(Collectors.toMap(
                        PlotEntity::getBlockNumber,
                        plot -> new BlockData(plot.getBlockNumber(), plot.getTotalAreaInM2(), plot.getBuiltAreaInM2()),
                        (existing, newPlot) -> {
                            existing.setTotalArea(existing.getTotalArea() + newPlot.getTotalArea());
                            existing.setBuiltArea(existing.getBuiltArea() + newPlot.getBuiltArea());
                            return existing;
                        }
                ));
        return new ArrayList<>(blockDataMap.values());
    }

    /**
     * Obtiene el conteo de propietarios por estado (activo/inactivo) por mes.
     *
     * @return un mapa donde la clave es el nombre del mes y el valor es otro mapa con el estado(activo/inactivo)
     * y el conteo de propietarios por ese estado
     */
    @Override
    public Map<String, Map<String, Long>> getOwnerCountByStatusPerMonth() {
        List<OwnerEntity> owners = ownerRepository.findAll();
        Map<String, Map<String, Long>> ownersCountByStatusPerMonth = owners.stream()
                .collect(Collectors.groupingBy(
                        owner -> owner.getCreatedDatetime().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        Collectors.groupingBy(
                                owner -> owner.getActive() ? "Activos" : "Inactivos",
                                Collectors.counting()
                        )
                ));
        return ownersCountByStatusPerMonth;
    }

    /**
     * Obtiene el porcentaje de propietarios por estado fiscal.
     * @return un mapa donde la clave es el estado fiscal y el valor es el porcentaje de propietarios en ese estado
     */
    @Override
    public Map<String, Double> getOwnerPercentageByTaxStatus() {
        List<OwnerEntity> owners = ownerRepository.findAll();
        long totalOwners = owners.size();
        Map<String, Long> countByTaxStatus = owners.stream()
                .collect(Collectors.groupingBy(
                        owner -> owner.getTaxStatus().getDescription(),
                        Collectors.counting()
                ));
        return countByTaxStatus.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totalOwners
                ));
    }


    /**
     * Obtiene la cantidad de lotes por el estado del lote.
     *
     * @return una lista con la cantidad de lotes por estado de lote.
     */
    @Override
    public List<PlotByPlotStateCountDTO> countPlotsByState() {
        List<Object[]> result = plotRepository.countPlotsByState();

        List<PlotByPlotStateCountDTO> dtoList = new ArrayList<>();

        for (Object[] row : result) {
            String stateName = (String) row[0];  // El primer valor del Object[] es el 'stateName'
            long count = (long) row[1];           // El segundo valor del Object[] es el 'count'
            dtoList.add(new PlotByPlotStateCountDTO(stateName, count));
        }
        return dtoList;
    }

    /**
     * Obtiene la cantidad de lotes por tipo de lote.
     *
     * @return una lista con la cantidad de lotes por tipo de lote.
     */
    @Override
    public List<PlotByPlotTypeCountDTO> countPlotsByType() {
        List<Object[]> result = plotRepository.countPlotsByType();
        List<PlotByPlotTypeCountDTO> dtoList = new ArrayList<>();
        for (Object[] row : result) {
            String typeName = (String) row[0];  // El primer valor es 'typeName'
            long count = ((Number) row[1]).longValue(); // Convertir a long de forma segura
            dtoList.add(new PlotByPlotTypeCountDTO(typeName, count));
        }
        return dtoList;
    }

    /**
     * Calcula las estadísticas generales de los lotes.
     *
     * @param startDate la fecha de inicio del filtro (opcional)
     * @param endDate la fecha de fin del filtro (opcional)
     * @param plotType el tipo de lote (opcional)
     * @param plotStatus el estado del lote (opcional)
     * @return un objeto con las estadísticas de los lotes.
     */
    @Override
    public PlotsStats getStatsOfPlots(LocalDate startDate, LocalDate endDate, String plotType, String plotStatus) {
        List<PlotEntity> plots = this.getFiltersPlots(startDate, endDate, plotType, plotStatus);

        long totalPlots = plots.size();
        long availablePlots = plots.stream().filter(plot -> plot.getPlotState().getId() == 1L).count();
        long constructionPlots = plots.stream().filter(plot -> plot.getPlotState().getId() == 3L).count();
        long occupiedPlots = plots.stream().filter(plot -> plot.getPlotState().getId() == 2L).count();
        double totalArea = plots.stream().mapToDouble(PlotEntity::getTotalAreaInM2).sum();
        double builtArea = plots.stream().mapToDouble(PlotEntity::getBuiltAreaInM2).sum();

        return new PlotsStats(totalPlots, availablePlots, constructionPlots, occupiedPlots, totalArea, builtArea);
    }

    /**
     * Agrupa los lotes por manzana y calcula sus estadísticas.
     *
     * @param startDate la fecha de inicio del filtro (opcional)
     * @param endDate la fecha de fin del filtro (opcional)
     * @return una lista con las estadísticas de los lotes agrupados por manzana.
     */
    @Override
    public List<PlotsByBlock> getPlotsByBlock(LocalDate startDate, LocalDate endDate) {
        List<PlotEntity> plots = this.getFiltersPlots(startDate, endDate, null, null);

        return plots.stream()
                .collect(Collectors.groupingBy(PlotEntity::getBlockNumber))
                .entrySet().stream()
                .map(plotsByBlock -> {
                    Integer blockNumber = plotsByBlock.getKey();
                    List<PlotEntity> blockPlots = plotsByBlock.getValue();
                    long availablePlots = blockPlots.stream().filter(plot -> plot.getPlotState().getId() == 2L).count();
                    long soldPlots = blockPlots.stream().filter(plot -> plot.getPlotState().getId() == 1L).count();
                    long inConstructionPlots = blockPlots.stream().filter(plot -> plot.getPlotState().getId() == 3L).count();
                    long totalPlots = blockPlots.size();
                    return new PlotsByBlock(blockNumber, availablePlots, soldPlots, inConstructionPlots, totalPlots);
                })
                .sorted(Comparator.comparing(PlotsByBlock::getBlockNumber))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la distribución de los lotes por propietario.
     *
     * @param startDate la fecha de inicio del filtro (opcional)
     * @param endDate la fecha de fin del filtro (opcional)
     * @param plotType el tipo de lote (opcional)
     * @param plotStatus el estado del lote (opcional)
     * @return una lista con las estadísticas de la distribución de los lotes por propietario.
     */
    @Override
    public List<OwnersPlotsDistribution> getOwnersPlotsDistribution(LocalDate startDate, LocalDate endDate,
                                                                    String plotType, String plotStatus) {
        List<PlotOwnerEntity> plotOwners = plotOwnerRepository.findAll();

        if (startDate != null && endDate != null) {
            plotOwners = plotOwners.stream()
                    .filter(plotOwner -> plotOwner.getPlot().getCreatedDatetime()
                            .isAfter(startDate.atStartOfDay()) && plotOwner.getPlot()
                            .getCreatedDatetime().isBefore(endDate.atStartOfDay().plusDays(1))).toList();
        }

        List<PlotEntity> plots = this.getFiltersPlots(null, null, plotType, plotStatus);

        List<Integer> filteredPlotIds = plots.stream()
                .map(PlotEntity::getId).toList();

        plotOwners = plotOwners.stream()
                .filter(plotOwner -> filteredPlotIds.contains(plotOwner.getPlot().getId()))
                .collect(Collectors.toList());
        return createOwnersPlotsDistribution(plotOwners);
    }

    /**
     * Obtiene las construcciones a lo largo de los años.
     *
     * @return una lista con las construcciones a lo largo de los años.
     */
    @Override
    public List<ConstructionProgress> getConstructionProgress() {
        List<PlotEntity> plots = plotRepository.findAll();

        return plots.stream()
                .filter(plot -> plot.getPlotState().getId() == 3L)
                .collect(Collectors.groupingBy(plot -> plot.getCreatedDatetime().getYear()))
                .entrySet().stream()
                .map(entry -> new ConstructionProgress(entry.getKey(), entry.getValue().size()))
                .sorted(Comparator.comparing(ConstructionProgress::getYear))
                .collect(Collectors.toList());
    }

    /**
     * Crea la distribución de los lotes por propietario.
     *
     * @param plotOwners la lista que tiene las relaciones entre propietarios y lotes.
     * @return una lista con las estadísticas de la distribución de los lotes por propietario.
     */
    @Override
    public List<OwnersPlotsDistribution> createOwnersPlotsDistribution(List<PlotOwnerEntity> plotOwners) {
        return plotOwners.stream()
                .collect(Collectors.groupingBy(PlotOwnerEntity::getOwner))
                .entrySet().stream()
                .map(entry -> {
                    OwnerEntity owner = entry.getKey();
                    List<PlotOwnerEntity> ownerPlots = entry.getValue();

                    String ownerName = owner.getName() == null ? "Sin Propietario" : (owner
                            .getOwnerType().getId() == 1L ? owner.getName() + " " + owner.getLastname() : owner.getBusinessName());

                    long countPlots = ownerPlots.size();
                    double totalArea = ownerPlots.stream()
                            .map(PlotOwnerEntity::getPlot)
                            .mapToDouble(PlotEntity::getTotalAreaInM2)
                            .sum();

                    return new OwnersPlotsDistribution(ownerName, countPlots, totalArea);
                })
                .sorted(Comparator.comparingLong(OwnersPlotsDistribution::getPlotCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Filtra los lotes según los parámetros recibidos.
     *
     * @param startDate la fecha de inicio del filtro (opcional)
     * @param endDate la fecha de fin del filtro (opcional)
     * @param plotType el tipo de lote (opcional)
     * @param plotStatus el estado del lote (opcional)
     * @return una lista con los lotes filtrados.
     */
    @Override
    public List<PlotEntity> getFiltersPlots(LocalDate startDate, LocalDate endDate, String plotType, String plotStatus) {
        List<PlotEntity> plots = plotRepository.findAll();
        if (startDate != null && endDate != null) {
            plots = plots.stream()
                    .filter(plot -> plot.getCreatedDatetime()
                            .isAfter(startDate.atStartOfDay()) && plot.getCreatedDatetime()
                            .isBefore(endDate.atStartOfDay().plusDays(1))).toList();
        }

        if (plotType != null && !plotType.isEmpty()) {
            plots = plots.stream()
                    .filter(plot -> plot.getPlotType().getName().equals(plotType))
                    .collect(Collectors.toList());
        }

        if (plotStatus != null && !plotStatus.isEmpty()) {
            plots = plots.stream()
                    .filter(plot -> plot.getPlotState().getName().equals(plotStatus))
                    .collect(Collectors.toList());
        }
        return plots;
    }
}