package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotStateDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.get.GetPlotTypeDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.post.PostPlotDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlotService {
    GetPlotDto createPlot(PostPlotDto postPlotDto);
    List<GetPlotStateDto> getPlotStates();
    List<GetPlotTypeDto> getPlotTypes();
    List<GetPlotDto> getAllPlots();

}
