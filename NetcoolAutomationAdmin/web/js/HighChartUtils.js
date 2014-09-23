HighChartUtils = function() {
    return{
        /**
         * Quando vocÊ entender o que este método faz ele não vai precisar 
         * de comentários para te explicar...
         * e você será etenernamente grato...
         * @param {string} target
         * @param {object} config
         */
        createChart: function(target, config) {
            chartObj = $('#' + target).highcharts({
                credits: {
                    enabled: false
                },
                chart: {
                    type: 'area'
                },
                title: {
                    text: 'Tempo Médio Total'
                },
                subtitle: {
                    text: 'Incidentes + Suspeitas dos últimos 30 dias'
                },
                xAxis: {
                    labels: {
                        rotation: -45,
                        formatter: function() {
                            return this.value; // clean, unformatted number for year
                        }
                    }
                },
                yAxis: {
                    title: {
                        text: 'Tempo médio em Hrs'
                    },
                    labels: {
                        formatter: function() {
                            return SASDashboard.sec2Hour(this.value);
                        }
                    }
                },
                tooltip: {
                    formatter: function() {
                        return SASDashboard.sec2Hour(this.y);
                    }
                },
                plotOptions: {
                    column: {
                        stacking: 'normal',
                        dataLabels: {
                            enabled: true,
                            color: 'white',
                            style: {
                                textShadow: '0 0 3px black, 0 0 3px black'
                            },
                            formatter: function() {
                                return SASDashboard.sec2Hour(this.y);
                            },
                            verticalAlign: 'top'
                        }
                    },
                    series: {
                        cursor: 'pointer',
                        point: {
                            events: {
                                click: function() {

                                }
                            }
                        }
                    }
                }
            });

            result = {
                chart: chartObj
            };
            return result;
        },
        /**
         * Atualiza o grafico xD
         * @returns {undefined}
         */
        refresh: function() {

        }
    };
}();