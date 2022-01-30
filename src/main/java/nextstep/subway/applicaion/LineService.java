package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.DuplicateException;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        if (isDuplicate(request.getName())) {
            throw new DuplicateException();
        }

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = Line.of(request, upStation, downStation, request.getDistance());
        Line createdLine = lineRepository.save(line);
        return LineResponse.of(createdLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final Long id) {
        return LineResponse.of(getLineById(id));
    }

    public void updateLine(final Long id, final LineRequest request) {
        Line line = getLineById(id);
        line.update(request.getName(), request.getColor());
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(final SectionRequest request, final Long id) {
        Line line = getLineById(id);
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Section newSection = Section.of(line, upStation, downStation, request.getDistance());
        line.addSection(newSection);
        return SectionResponse.of(newSection);
    }

    public void deleteSection(final Long lineId, final Long downStationId) {
        Line line = getLineById(lineId);
        line.removeSection(downStationId);
    }

    private Line getLineById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineException::new);
    }

    private boolean isDuplicate(final String lineName) {
        Optional<Line> station = lineRepository.findByName(lineName);
        return station.isPresent();
    }

    private Station findStationById(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(NotFoundStationException::new);
    }
}
