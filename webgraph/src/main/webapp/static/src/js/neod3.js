/**
 * Created by ACT-NJ on 2017/7/7.
 */


function idIndex(a,id){
    for(var i=0;i<a.length;i++){
        if(a[i].id == id)
            return i;
    }
    return null;
}
function drawneoGraphD3(targets,query,rawdata) {
    console.log(d3.version);
    var width = 1280;
    var height = 800;
    nodes = [];
    links = [];
    if (rawdata && rawdata.length == 0) {
        var temp = {
            id: 'null',
            _name: query
        };
        nodes.push(temp)
    } else {
        $.each(rawdata, function (i, item) {
            $.each(item.graph.nodes, function (j, object) {
                if (idIndex(nodes, object.id) == null) {
                    var nodeSingle = {
                        id: object.id,
                        _name: object.properties.name,
                        _time: object.properties.time
                    };
                    nodes.push(nodeSingle);
                }
            });
            if (item.graph.relationships.length != 0) {
                links.push({
                    type:item.graph.relationships[0].type,
                    source: idIndex(nodes, item.graph.relationships[0].startNode),
                    target: idIndex(nodes, item.graph.relationships[0].endNode)
                });
            }
        });
    }
    var force = d3.layout.force()  //力布局画布
        .size([width, height])  //作用域大小
        .linkDistance(90)  //顶点间距离
        .charge(-300)  //斥力大小
        .on("tick", tick);
    var drag = force.drag().on("dragstart", dragstart);
    var color = d3.scale.category10()
    var svg = d3.select(targets).append("svg").attr("width", width).attr("height", height);
    var tooltip = d3.select("body")
        .append("div")
        .attr("class","tooltip")
        .style("opacity",0.0);
    var svg_nodes = svg.selectAll("circle")
        .data(nodes)
        .enter()
        .append("circle")
        .attr("r", 20)
        .attr("class", "node")
        // .style("fill", function (d, i) {
        //     return color(i);
        // })
        // .on("mouseover",function(d,i){
        //     alert(rawdata[i].graph.nodes[0].properties);
        // })
        .on("dblclick", dblclick)
        .call(drag);

    var svg_texts = svg.selectAll("text")
        .data(nodes)
        .enter()
        .append("text")
        .style("fill", "black")
        .attr("dx", 20)
        .attr("dy", -20)
        .text(function(d){
            return  d._name;
        });

    var svg_edges = svg.selectAll("line")
        .data(links)
        .enter()
        .append("line")
        .attr("class", "link")
        .style("stroke", "#ccc")
        .style("stroke-width", 1.5);

    force.nodes(nodes).links(links).start();

    function dblclick(d) {
        d3.select(this).classed("fixed", d.fixed = false);
    }

    function dragstart(d) {
        d3.select(this).classed("fixed", d.fixed = true);
    }

    function tick() {
        svg_edges.attr("x1", function (d) {
            return d.source.x;
        }).attr("y1", function (d) {
            return d.source.y;
        }).attr("x2", function (d) {
            return d.target.x;
        }).attr("y2", function (d) {
            return d.target.y;
        });

        svg_nodes.attr("cx", function (d) {
            return d.x;
        }).attr("cy", function (d) {
            return d.y;
        });

        svg_texts.attr("x", function(d){ return d.x; })
            .attr("y", function(d){ return d.y; });

    }
}

function showProperties() {

}