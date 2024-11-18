package ar.edu.utn.frc.tup.lc.iv.repositories;


import ar.edu.utn.frc.tup.lc.iv.entities.PlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Conecta la aplicación con la base de datos para manejar lotes.
 */
@Repository
public interface PlotRepository extends JpaRepository<PlotEntity, Integer> {

    /**
     * Busca un lote por el número de lote.
     *
     * @param plotNumber el número del lote.
     * @return un {@link PlotEntity}
     */
    PlotEntity findByPlotNumber(int plotNumber);

    /**
     * Busca una lista de lotes que estén disponibles.
     *
     * @return una lista de {@link PlotEntity}.
     */
    @Query("SELECT p FROM PlotEntity p WHERE p.plotState.name = 'Disponible'")
    List<PlotEntity> findPlotsAvailables();

    /**
     * Busca si existe un lote coincidente a un numero de lote.
     *
     * @param plotNumber numero de lote.
     * @return un booleano.
     */
    boolean existsByPlotNumber(int plotNumber);

    /**
     * Cuenta la cantidad de lotes agrupados por su estado.
     *
     * @return una lista de objetos donde cada uno contiene el nombre del estado y el conteo de lotes en ese estado.
     */
    @Query(value = "SELECT ps.name as stateName, COUNT(p.id) as count "
            + "FROM plots p "
            + "JOIN plot_states ps ON ps.id = p.plot_state_id "
            + "GROUP BY ps.name",
            nativeQuery = true)
    List<Object[]> countPlotsByState();

    /**
     * Cuenta la cantidad de lotes agrupados por su tipo.
     *
     * @return una lista de objetos donde cada uno contiene el nombre del tipo y el conteo de lotes de ese tipo.
     */
    @Query(value = "SELECT pt.name AS typeName, COUNT(p.id) AS count "
            + "FROM plots p "
            + "JOIN plottypes pt ON pt.id = p.plot_type_id "
            + "GROUP BY pt.name", nativeQuery = true)
    List<Object[]> countPlotsByType();

}
