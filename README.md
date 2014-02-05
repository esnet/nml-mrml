nml-mrs-model
=============

### DOE RAINS Project

Network Markup Language (NML) - Multi-Resource Service (MRS) - Modeling schemas and tools

### Modeling Design Notes

1. Adopting the NML structure, we describe network topology by using three categories of elements: Resource, Service and Relation. NML Resources include Topology, Node, Port, Link, Label and Group etc. These Objects have hierarchies on their owns. Topology could contain another layer of Topologies. Node can contain other Nodes. PortGroup can contain other Port and PortGroup. Such recursive hierarchies form the basis for defining physical and technology layers as well as virtualizations and overlays.  

2. Virtualization Hierarchies = 
        Resources are hierarchical as "Nested Boxes" provided by Services
        Services are hierarchical as "Layered Instances" associated with Resources
 
3. Virtualization / Overlay / Abstraction / Hierarchy =
        Resource (Box) contains Service(s) and sub-level Resources. An Instance of client Service is instantiated by "checking" the contained Services along the path of Resources (Boxes). An Instance is allocated with certain capacity by the provider Services and may also be allocated with dedicated sub-level Resources. Every resource manager (RM) locally manages the allocation. client Service can become the Service associated with virtualized Resource (Nested Box) that forms an abstract/higher level of topology.

4. Implementation = 
        Resource Service Provider (RSP) uses the same Resource-Service model for internal resource management and external interface. For internal managing, the Service capacity and capability states are more granular, dynamic and real-time. For external interface, a reduced or abstract view of the states are exposed. For both internal and external operations, it can use the same service reference and same access methods to avoid translation overhead.
 
5. In this extension, it is necessary to re-introduce the “Capability” elements that NDL (pre-NML) used to both the Node and Port levels. As a service oriented design, we define each capability as a Service. For example, generic capacity or bandwidth is a Service. Protection capability is a Service. Technology and vendor specific capabilities are also defined as Services. Combining these Services with above hierarchies of NML Resources through Relations, we can easily define complex multi-resource topologies.

